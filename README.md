# OmniGraph
Desktop application for creating graphs and algorithms visualisation build in JavaFX

![exapmple1](https://github.com/Todense/OmniGraph/blob/master/src/main/resources/screenshots/random.png)


## Main Features

* Create and edit interactive graphs
* Visualize basic graph algorithms
* Arrange graphs with the force-directed layout
* Generate random graphs using different models
* Create build-in graph presets 
* Visualize ant colony algorithms for solving the travelling salesman problem
* Save & open graphs

## How To Run
To run OmniGraph you will need Java 11+

Download jar file from [latest release](https://github.com/Todense/OmniGraph/releases), or run with maven:
```
git clone https://github.com/Todense/OmniGraph.git
cd OmniGraph
mvn clean javafx:run
```


## Build with

* Java 11
* [OpenJFX](https://openjfx.io/) 
* [Maven](https://maven.apache.org/) - dependency management
* [Scene Builder](https://gluonhq.com/products/scene-builder/) - visual layout tool for JavaFX Applications
* [MvvmFX](https://github.com/sialcasa/mvvmFX) - an application framework for implementing the Model-View-ViewModel Pattern for JavaFX
* [ControlsFX](https://github.com/controlsfx/controlsfx) - custom JavaFX controls
* [JFoenix](https://github.com/jfoenixadmin/JFoenix) - JavaFX material design library
* [Ikonli](https://github.com/kordamp/ikonli) - icon packs for Java applications
* [JUnit 5](https://junit.org/junit5/) - Java testing framework
* CSS

## Additional info

Force directed layout algorithm is based on: [Efficient, High-Quality Force-Directed Graph Drawing](https://www.researchgate.net/publication/235633159_Efficient_and_High_Quality_Force-Directed_Graph_Drawing).

Ant colony algorithms are based on [ACO Algorithms for the Traveling Salesman Problem](https://www.researchgate.net/publication/2771967_ACO_Algorithms_for_the_Traveling_Salesman_Problem)

Supported file formats: 
* .ogr - OmniGraph custom format (save node positions, node labels, edge weights and colors)
* .tsp -  [TSPLIB](http://comopt.ifi.uni-heidelberg.de/software/TSPLIB95/) format for the travelling salesman problem (only EUC_2D edge weight type)
* .mtx - [Matrix Market](https://math.nist.gov/MatrixMarket/) format (save structure of a graph)
* .graphml - [GraphML](http://graphml.graphdrawing.org/) format (save node positions)



## Screenshots

![layout](https://github.com/Todense/OmniGraph/blob/master/src/main/resources/screenshots/layout.png) 
![random2](https://github.com/Todense/OmniGraph/blob/master/src/main/resources/screenshots/random_big.png)
![complete](https://github.com/Todense/OmniGraph/blob/master/src/main/resources/screenshots/complete.png)
![astar](https://github.com/Todense/OmniGraph/blob/master/src/main/resources/screenshots/astar.png)
![prim](https://github.com/Todense/OmniGraph/blob/master/src/main/resources/screenshots/prim.png)
![ant colony algorithm](https://github.com/Todense/OmniGraph/blob/master/src/main/resources/screenshots/ant.png)










