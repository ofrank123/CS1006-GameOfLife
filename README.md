# Conway's Game of Life++

## Build Instructions

Because the gradle wrapper install gradle locally and also installs the
dependencies (JavaFX),  you shouldn&rsquo;t need to install anything yourself.


### Linux/Mac

1.  Clone the repository ( `hg clone https://of9.hg.cs.st-andrews.ac.uk/GameOfLife` )
2.  Run `./gradlew run` from the top level of the directory


### Windows

1.  Clone the repository ( `https://of9.hg.cs.st-andrews.ac.uk/GameOfLife` )
2.  Run `.\gradlew.bat run` from the top level of the directory in
    PowerShell


## Usage Instructions

Most of the program has already been described, but there are a few things to
note about using it.  Because of the slowness experienced when using the 3D
mode, it is best to use the advance tick in the beginning, until the amount of
voxels has thinned out.  In the case of Fig6.gol and Fig7.gol, the play/pause
button should not be used, as it will slow and then freeze the program.  With
Fig5.gol it should be fine to use the play button.

For some interesting cellular automata to try, check the following:

-   <http://psoup.math.wisc.edu/mcell/rullex_gene.html>
-   <https://softologyblog.wordpress.com/2019/12/28/3d-cellular-automata-3/>
