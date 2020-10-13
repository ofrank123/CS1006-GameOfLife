import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class MainWindow extends Application {
    IO openSave;

    GridPane layout;
    GraphicsRegion gr;
    Canvas eraseLight;
    Canvas playLight;
    Slider resizeSlider;

    // Some initial constants
    boolean play = false;
    boolean erase = false;
    boolean in3d = false;
    int speed = 100;
    int size = 50;
    int fillPercent = 0;

    public static void main(String[] args) {
        GameLogic2D.setSingleton(new GameLogic2D(0));
        GameLogic3D.setSingleton(new GameLogic3D(20));

        Rule.setSingleton(Rule.parseRuleString("2,3/3/2/M", false));
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Conway's Game of Life++");

        GridPane buttons = new GridPane();

        // PLAY/PAUSE BUTTON
        Button playButton = new Button();
        playButton.setText("Play/Pause");
        playButton.setOnAction((event) -> {
                play = !play;
                switchPlayLight();
                if (play) {
                    Thread runner = new Thread() {
                            public void run() {
                                runGame();
                            }
                        };
                    runner.start();
                }
            });
        GridPane.setConstraints(playButton, 0, 0);

        // Playing indicator light
        Label playLabel = new Label("Playing:");
        playLabel.setTextFill(Color.WHITE);
        GridPane.setConstraints(playLabel, 0, 1);
        GridPane.setHalignment(playLabel, HPos.CENTER);

        playLight = new Canvas(55, 24);
        switchPlayLight();
        GridPane.setConstraints(playLight, 0, 2);
        GridPane.setHalignment(playLight, HPos.CENTER);
        GridPane.setValignment(playLight, VPos.CENTER);

        // 2D/3D toggle button
        Button toggleDim = new Button();
        toggleDim.setText("2D/3D");
        toggleDim.setOnAction((event) -> {
            in3d = !in3d;
            if (in3d) {
                Rule.setSingleton(Rule.parseRuleString(Rule.getSingleton().getRuleString(), true));
                layout.getChildren().remove(gr.getNode());
                gr = new Graphics3D(750, fillPercent);
                GridPane.setHalignment(gr.getNode(), HPos.CENTER);
                GridPane.setConstraints(gr.getNode(), 0, 1);
                layout.getChildren().add(gr.getNode());
                resizeSlider.setMax(50);
                size = Math.min(size, 50);
            } else {
                try {
                    Rule.setSingleton(Rule.parseRuleString(Rule.getSingleton().getRuleString(), false));
                } catch (IllegalArgumentException e) {
                    Rule.setSingleton(Rule.parseRuleString("2,3/3/2/M", false));
                }
                layout.getChildren().remove(gr.getNode());
                gr = new GameCanvas(750, fillPercent);
                GridPane.setHalignment(gr.getNode(), HPos.CENTER);
                GridPane.setConstraints(gr.getNode(), 0, 1);
                layout.getChildren().add(gr.getNode());
                resizeSlider.setMax(200);
            }
        });
        GridPane.setConstraints(toggleDim, 0, 3);

        // ADVANCE TICK BUTTON
        Button advanceButton = new Button();
        advanceButton.setText("Advance Tick");
        advanceButton.setOnAction((event) -> {
                gr.update();
        });
        GridPane.setConstraints(advanceButton, 1, 0);

        // SPEED LABEL
        Label speedLabel = new Label("Speed:");
        speedLabel.setTextFill(Color.WHITE);
        GridPane.setConstraints(speedLabel, 1, 1);

        // SPEED SLIDER
        int slideMax = 1000000;
        int slideMin = 50;
        Slider playSlider = new Slider(slideMin, slideMax, slideMax / 2);
        playSlider.valueProperty().addListener((o, oldval, newval) -> {
                speed = (int) ((Math.log(slideMax) - Math.log(newval.intValue())) * 100) + 25;
        });
        playSlider.setShowTickMarks(false);
        playSlider.setShowTickLabels(false);
        GridPane.setConstraints(playSlider, 1, 2);

        // SAVE BUTTON
        Button saveButton = new Button();
        saveButton.setText("Save");
        saveButton.setOnAction((event) -> {
             if(!in3d)
                 openSave = new IO(primaryStage, Rule.getSingleton(), GameLogic2D.getSingleton(), in3d);
             else
                 openSave = new IO(primaryStage, Rule.getSingleton(), GameLogic3D.getSingleton(), in3d);

            openSave.saveFile();

            });
        GridPane.setConstraints(saveButton, 1, 3);

        // RESET BUTTON
        Button resetButton = new Button();
        resetButton.setText("Reset");
        resetButton.setOnAction((event) -> {
                        gr.reset(size, fillPercent);
                        System.out.println(fillPercent);
            });
        GridPane.setConstraints(resetButton, 2, 0);

        // SIZE LABEL
        Label resizeLabel = new Label("Size:");
        resizeLabel.setTextFill(Color.WHITE);
        GridPane.setConstraints(resizeLabel, 2, 1);

        // SIZE SLIDER
        resizeSlider = new Slider(5, 200, 50);
        resizeSlider.valueProperty().addListener((o, oldval, newval) -> {
                size = newval.intValue();
            });
        GridPane.setConstraints(resizeSlider, 2, 2);


        // LOAD BUTTON
        Button loadButton = new Button();
        loadButton.setText("Load");
        loadButton.setOnAction((event) -> {
             if(!in3d)
                 openSave = new IO(primaryStage, Rule.getSingleton(), GameLogic2D.getSingleton(), in3d);
             else
                 openSave = new IO(primaryStage, Rule.getSingleton(), GameLogic3D.getSingleton(), in3d);

             try {
                 LoadResult lr = openSave.openFile(gr, layout, resizeSlider);
                 in3d = lr.getIn3d();
                 gr = lr.getGR();
                 size = lr.getSize();
             } catch (IOException e) {
                 System.out.println("Couldn't open file");
             }
            });
        GridPane.setConstraints(loadButton, 2, 3);

        // COLOR TOGGLE BUTTON
        Button colorButton = new Button();
        colorButton.setText("Toggle Colors");
        colorButton.setOnAction((event) -> {
                gr.toggleColors();
            });
        GridPane.setConstraints(colorButton, 3, 0);

        // FILL LABEL
        Label percentLabel = new Label("Fill Percentage:");
        percentLabel.setTextFill(Color.WHITE);
        GridPane.setConstraints(percentLabel, 3, 1);

        // SPEED SLIDER
        Slider percentSlider = new Slider(0, 101, 0);
        percentSlider.valueProperty().addListener((o, oldval, newval) -> {
                fillPercent = newval.intValue();
        });
        percentSlider.setShowTickMarks(false);
        percentSlider.setShowTickLabels(false);
        GridPane.setConstraints(percentSlider, 3, 2);

        // ERASE BUTTON
        Button eraseButton = new Button();
        eraseButton.setText("Toggle Erasing");
        eraseButton.setOnAction((event) -> {
                erase = !erase;
                switchEraseLight();
                gr.toggleErase();
            });
        GridPane.setConstraints(eraseButton, 4, 0);

        // ERASE INDICATOR LIGHT
        eraseLight = new Canvas(55, 25);
        switchEraseLight();
        GridPane.setConstraints(eraseLight, 4, 2);
        GridPane.setHalignment(eraseLight, HPos.CENTER);
        GridPane.setValignment(eraseLight, VPos.CENTER);

        // RULE LABEL
        Label rulesLabel = new Label("Rule String:");
        rulesLabel.setTextFill(Color.WHITE);
        GridPane.setConstraints(rulesLabel, 5, 1);

        // RULE CHANGE ERROR LABEL
        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);
        GridPane.setConstraints(errorLabel, 5, 3);

        // RULE CHANGE FIELD
        TextField rulesField = new TextField();
        GridPane.setConstraints(rulesField, 5, 2);

        // RULE CHANGE BUTTON
        // Needs to be after errorLabel and rulesField
        Button rulesButton = new Button("Change Rules");
        rulesButton.setOnAction((e) -> {
                if ((rulesField.getText() != null && !rulesField.getText().isEmpty())) {
                    try {
                        Rule.setSingleton(Rule.parseRuleString(rulesField.getText(), in3d));
                        errorLabel.setText("Rule changed!");
                        gr.reset(size, fillPercent);
                    } catch (IllegalArgumentException ex) {
                        errorLabel.setText("Invalid rule String!");
                        rulesField.clear();
                    }
                }
            });
        GridPane.setConstraints(rulesButton, 5, 0);

        // ADD ALL OF THE BUTTONS AND OTHER ELEMENTS
        buttons.getChildren().addAll(eraseButton,
                                     saveButton,
                                     advanceButton,
                                     playButton,
                                     resetButton,
                                     playSlider,
                                     speedLabel,
                                     resizeLabel,
                                     resizeSlider,
                                     colorButton,
                                     playLight,
                                     playLabel,
                                     eraseLight,
                                     percentLabel,
                                     percentSlider,
                                     rulesLabel,
                                     rulesField,
                                     errorLabel,
                                     rulesButton,
                                     toggleDim,
                                     loadButton
                                     );
        buttons.setHgap(5);
        GridPane.setConstraints(buttons, 0, 0);

        // Setup the graphics region
        gr = new GameCanvas(750, fillPercent);
        GridPane.setHalignment(gr.getNode(), HPos.CENTER);
        GridPane.setConstraints(gr.getNode(), 0, 1);

        // Setup the layout
        layout = new GridPane();
        layout.setStyle("-fx-background-color: #262626;");
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setVgap(10);

        // Add the 2 elements
        layout.getChildren().addAll(buttons, gr.getNode());

        // Setup the main scene
        Scene scene = new Scene(layout, 770, 825);
        primaryStage.setScene(scene);
        primaryStage.show();
        // Add listeners for the resize
        primaryStage.widthProperty().addListener((o, oldval, newval) -> {
                resize(primaryStage);
            });

        primaryStage.heightProperty().addListener((o, oldval, newval) -> {
                resize(primaryStage);
            });

        // Correct the size if necessary
        resize(primaryStage);
    }

    public void resize(Stage primaryStage) {
        // Magic numbers used to resize
        int width = (int) primaryStage.getWidth() - 30;
        int height = (int) primaryStage.getHeight() - 150;
        if (height > width) {
            gr.resize(width);
        } else {
            gr.resize(height);
        }
    }

    public void switchPlayLight() {
        GraphicsContext context = playLight.getGraphicsContext2D();
        if (play) {
            context.setFill(Color.LIME);
        } else {
            context.setFill(Color.RED);
        }
        context.setStroke(Color.WHITE);
        context.setLineWidth(2);
        context.fillRoundRect(2, 2, 50, 20, 10, 10);
        context.strokeRoundRect(2, 2, 50, 20, 10, 10);
    }

    public void switchEraseLight() {
        GraphicsContext context = eraseLight.getGraphicsContext2D();
        if (erase) {
            context.setFill(Color.BLACK);
        } else {
            context.setFill(Color.WHITE);
        }
        context.setStroke(Color.WHITE);
        context.setLineWidth(2);
        context.fillRoundRect(2, 2, 50, 20, 10, 10);
        context.strokeRoundRect(2, 2, 50, 20, 10, 10);
    }

    public void runGame() {
        while (play) {
            gr.update();
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                System.err.println("ERROR");
            }
        }
    }
}
