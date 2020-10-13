import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.geometry.HPos;


public class IO{

    //Attributes
    int size;
    Stage mainStage;
    boolean in3D;

    //Constructors
    public IO(Stage stage, Rule rule, boolean in3D){
        Rule.setSingleton(rule);
        mainStage = stage;
        this.in3D = in3D;
    }

    public IO(Stage stage, Rule rule, GameLogic2D gamelogic, boolean in3D){
        this(stage, rule, in3D);
        GameLogic2D.setSingleton(gamelogic);
        size = GameLogic2D.getSingleton().getSize();
    }

    public IO(Stage stage, Rule rule, GameLogic3D gamelogic, boolean in3D){
        this(stage, rule, in3D);
        GameLogic3D.setSingleton(gamelogic);
        size = GameLogic3D.getSingleton().getSize();
    }

    public int getSize(){
        return size;
    }

    //Saving file using FileChooser
    public void saveFile(){
        try{

            //Makes the directory if it doesn't already exist
            File directory = new File("./saved");
            if(!directory.exists())
                directory.mkdir();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("./saved"));       //Opens filechooser in the directory
            fileChooser.setTitle("Save .gol File");
            fileChooser.getExtensionFilters().addAll(                   //Allows to only see/overwrite .gol files
                new ExtensionFilter("Patterns", "*.gol"));

            File selectedFile = fileChooser.showSaveDialog(mainStage);

            if (selectedFile != null) {
                FileWriter f;

                //Making sure that only .gol files are saved
                if(!selectedFile.getName().endsWith(".gol")){
                    f = new FileWriter(selectedFile.getPath() + ".gol"); 
                }else{
                    f = new FileWriter(selectedFile);
                }
    
                //Saving current rule, size and if the file saving is supposed to be 3D
                f.write(Rule.getSingleton().getRuleString() + "\n" + size
                        + "\n" + Boolean.toString(in3D) + "\n");

                //Writing to the file
                for(int x = 0; x < size; x++){
                    if(!in3D){
                        if(Rule.getSingleton().getMaxState() == 1){
                            f.write(convertToChar_twoStates(x), 0, size+1);
                        }
                        else{
                            f.write(convertToString(x));
                        }
                    }
                    else{
                        for(int y = 0; y < size; y++){
                            f.write(convertToString3D(x, y));
                        }
                    }
                }
            f.close();
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    //Converting state to string to be saved
    //This one is as per the practical spec and in case of two states
    public char[] convertToChar_twoStates(int rowNo){
        char[] rowAsChar = new char[size+1];
        for(int y = 0; y < size; y++){
            if(GameLogic2D.getSingleton().getState(rowNo, y) == 0){
                rowAsChar[y] = '.';
            }
            else{
                rowAsChar[y] = 'o';
            }
        }
        rowAsChar[size] = '\n';
        return rowAsChar;
    }

    //This one is for where states are not equal than 2
    public String convertToString(int x){
        String line = "";
        for(int y = 0; y < size ; y++){
            line += GameLogic2D.getSingleton().getState(x, y) + " ";
        }
        line += '\n';
        return line;
    }

    //For 3D
    public String convertToString3D(int x, int y){
        String line = "";
        for(int z = 0; z < size ; z++){
            line += GameLogic3D.getSingleton().getState(x, y, z) + " ";
        }
        line += '\n';
        return line;
    }

    //Opening file
    public LoadResult openFile(GraphicsRegion gr, GridPane layout, Slider slider) throws IOException {

        //Similar as before
       File directory = new File("./saved");
        if(!directory.exists())
            directory.mkdir();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open .gol File");
        fileChooser.setInitialDirectory(new File("./saved"));
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Patterns", "*.gol"));
        File selectedFile = fileChooser.showOpenDialog(mainStage);
        if (selectedFile != null) {
            return read(selectedFile, gr, layout, slider);
        } else {
            throw new IOException("File not found!");
        }
    }

    //Reading the file
    public LoadResult read(File selectedFile, GraphicsRegion gr, GridPane layout, Slider resizeSlider) throws IOException {
        List<String> allLines;
        try {
            allLines = Files.readAllLines(selectedFile.toPath());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

        in3D = Boolean.parseBoolean(allLines.get(2));
        Rule.setSingleton(Rule.getSingleton().parseRuleString(allLines.get(0), in3D));
        size = Integer.parseInt(allLines.get(1));

        allLines.remove(0);
        allLines.remove(0);
        allLines.remove(0);

        if(!in3D){
            layout.getChildren().remove(gr.getNode());
            gr = new GameCanvas(750, 0);
            GridPane.setHalignment(gr.getNode(), HPos.CENTER);
            GridPane.setConstraints(gr.getNode(), 0, 1);
            layout.getChildren().add(gr.getNode());
            resizeSlider.setMax(200);
            gr.reset(size, 0);
            read2d(allLines);
            return new LoadResult(false, size, gr);
        }
        else{
            layout.getChildren().remove(gr.getNode());
            gr = new Graphics3D(750, 0);
            GridPane.setHalignment(gr.getNode(), HPos.CENTER);
            GridPane.setConstraints(gr.getNode(), 0, 1);
            layout.getChildren().add(gr.getNode());
            resizeSlider.setMax(50);
            size = Math.min(size, 50);
            gr.reset(size, 0);
            read3d(allLines);
            gr.draw();
            return new LoadResult(true, size, gr);
        }
    }

    public void read2d(List<String> allLines){

        if(Rule.getSingleton().getMaxState() == 1){
            int state;
            for(int x = 0; x < allLines.size(); x++){
                String str = allLines.get(x);
                for(int y = 0; y < str.length(); y++){
                    Point p = new Point();
                    if(str.charAt(y) == 'o'){
                        state = 1;
                    }
                    else{
                        state = 0;
                    }
                    p.setState(state);
                    GameLogic2D.getSingleton().setPos(x, y, p);
                }
            }
        }
        else{
            for(int x = 0; x < allLines.size(); x++){
                String str = allLines.get(x);
                String[] states = str.split(" ");
                for(int y = 0; y < states.length; y++){
                    Point p = new Point();
                    p.setState(Integer.parseInt(states[y]));
                    GameLogic2D.getSingleton().setPos(x, y, p);
                }
            }
        }
    }

    public void read3d(List<String> allLines) {
        int c = 0;
        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                String str = allLines.get(c);
                c++;
                String[] states = str.split(" ");
                for(int z = 0; z < size; z++){
                    Point p = new Point();
                    p.setState(Integer.parseInt(states[z]));
                    GameLogic3D.getSingleton().setPos(x, y, z, p);
                }
            }
        }

    }

}
 
