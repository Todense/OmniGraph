package com.todense.viewmodel.canvas;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.popover.PopOverManager;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.InputScope;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.controlsfx.control.PopOver;

import java.util.ArrayList;
import java.util.Set;

public class MouseHandler {

    private GraphManager GM;
    private GraphScope graphScope;
    private Painter painter;
    private Camera camera;
    private InputScope inputScope;

    PopOverManager popOverManager;
    PopOver popOver;

    private Point2D mousePressPt;
    private Point2D currentMousePt;

    private Node edgeStartNode;
    private Node edgeEndNode;

    private Node clickedNode; //clicked node
    private Edge clickedEdge; //clicked edge

    private Node hoverNode; //node under mouse
    private Edge hoverEdge; //edge under mouse

    private boolean dragging = false;
    private boolean draggingNode = false;

    private final ArrayList<Edge> selectedEdges = new ArrayList<>();

    private Set<KeyCode> pressedKeys;

    public MouseHandler(Camera camera, InputScope inputScope,
                        Painter painter, GraphScope graphScope,
                        PopOverManager popOverManager, Set<KeyCode> pressedKeys){
        this.camera = camera;
        this.inputScope = inputScope;
        this.painter = painter;
        this.graphScope = graphScope;
        this.GM = graphScope.getGraphManager();
        this.popOverManager = popOverManager;
        this.pressedKeys = pressedKeys;
    }

    //-----------------------------------------------------
    //-------------------- MOUSE EVENTS -------------------
    //-----------------------------------------------------

    public void onMousePressed(MouseEvent event){

        mousePressPt = new Point2D(event.getX(), event.getY());

        if(inputScope.isEditLocked()) return;

        hidePopOver();

        dragging = false;

        this.clickedNode = getNodeFromPoint(mousePressPt);
        this.clickedEdge = getEdgeFromPoint(mousePressPt);


        if(event.getButton() == MouseButton.PRIMARY){
            if(clickedNode != null){
                draggingNode = true;
            }
        }

        if(event.getButton() == MouseButton.SECONDARY) {   //RIGHT MOUSE BUTTON
            inputScope.setSelecting(false);
            if (clickedNode == null && clickedEdge == null) {  //start selection rectangle
                clearSelection();
                inputScope.setRectStartX(mousePressPt.getX());
                inputScope.setRectStartY(mousePressPt.getY());
            }
            else if (clickedNode != null){      //start dummy edge
                edgeStartNode = clickedNode;
                if(clickedNode.isSelected()){
                    inputScope.getDummyEdgeStartNodes().addAll(GM.getSelectedNodes());
                }else {
                    inputScope.getDummyEdgeStartNodes().add(clickedNode);
                }
                inputScope.setDummyEdgeEnd(edgeStartNode.getPos());
            }
        }

        if(event.getButton() == MouseButton.MIDDLE) {   // MIDDLE MOUSE BUTTON
            if(clickedNode != null){
                GM.getGraph().removeNode(clickedNode);
            }
            else if(clickedEdge != null){
                GM.getGraph().removeEdge(clickedEdge);
            }
        }
        painter.repaint();
    }

    public void onMouseClicked(MouseEvent event) {

        if(inputScope.isEditLocked()) return;

        if(event.getButton() == MouseButton.PRIMARY && !dragging) {  // LEFT MOUSE BUTTON
            if(!pressedKeys.contains(KeyCode.SHIFT)){
                if(clickedNode == null && clickedEdge == null){  // click on background
                    if(pressedKeys.contains(KeyCode.CONTROL) && pressedKeys.contains(KeyCode.V)){
                        Point2D transPt = camera.inverse(new Point2D(event.getX(), event.getY()));
                        GM.addSubgraph(transPt);
                    }
                    else{
                        GM.getGraph().addNode(camera.inverse(mousePressPt));
                    }

                }
                else if(pressedKeys.contains(KeyCode.CONTROL)){
                    if(pressedKeys.contains(KeyCode.C)){
                        graphScope.getGraphManager().copySelectedSubgraph();
                    }
                    else{
                        if(clickedNode != null){
                            reverseSelection(clickedNode);
                        }
                        else{
                            reverseSelection(clickedEdge);
                        }
                    }
                }
            }
        }
    }

    public void onMouseReleased(MouseEvent event){

        if(inputScope.isEditLocked()) return;

        draggingNode = false;

        Point2D releasePt = new Point2D(event.getX(), event.getY());

        Node releaseNode = getNodeFromPoint(releasePt);
        Edge releaseEdge = getEdgeFromPoint(releasePt);

        if(event.getButton() == MouseButton.SECONDARY) {
            if(inputScope.isConnecting()){
                if (releaseNode != null) {
                    for(Node n : inputScope.getDummyEdgeStartNodes()){ // add/remove edge
                        if(n.equals(releaseNode))
                            continue;
                        if (!GM.isEdgeBetween(releaseNode, n)) {
                            GM.getGraph().addEdge(releaseNode, n);
                        } else {
                            GM.getGraph().removeEdge(releaseNode, n);
                        }
                        n.setHighlighted(false);
                    }
                    releaseNode.setHighlighted(false);
                }
                inputScope.getDummyEdgeStartNodes().clear();
            }

            else if(!inputScope.isSelecting()){
                if (releaseNode != null) {         //node popover
                    if (!releaseNode.isSelected()) {
                        clearSelection();
                        releaseNode.setSelected(true);
                        GM.getSelectedNodes().add(releaseNode);
                    }
                    popOver = popOverManager.createNodePopOver(graphScope.getGraphManager(), releaseNode,
                            GM.getSelectedNodes(), event.getScreenX(), event.getScreenY());
                } else if (releaseEdge != null) {  //edge popover
                    if (!releaseEdge.isSelected()) {
                        clearSelection();
                        releaseEdge.setSelected(true);
                        selectedEdges.add(releaseEdge);
                    }
                    popOver = popOverManager.createEdgePopOver(graphScope.getGraphManager(),
                            selectedEdges, event.getScreenX(), event.getScreenY());
                } else{
                    Point2D transPt = camera.inverse(new Point2D(event.getX(), event.getY()));
                    popOver = popOverManager.createBackgroundPopOver(event.getScreenX(), event.getScreenY(), transPt);
                }
            }

            inputScope.setConnecting(false);

            if(inputScope.isSelecting()){
                inputScope.setSelecting(false);
                GM.createSelectedNodesList();

                for(Edge edge : GM.getGraph().getEdges()){
                    edge.setMarked(edge.getN1().isSelected() && edge.getN2().isSelected());
                }
            }
            edgeStartNode = null;
        }
        inputScope.getDummyEdgeStartNodes().clear();
        painter.repaint();
    }


    public void onMouseMoved(MouseEvent event){

        Point2D movePt = new Point2D(event.getX(), event.getY());
        currentMousePt = movePt;

        if(inputScope.isEditLocked()) return;

        Node prevNode = hoverNode;
        hoverNode = getNodeFromPoint(movePt);

        //unhighlight previous node if it is not equal to the present
        if(prevNode != null && !prevNode.equals(hoverNode)){
            prevNode.setHighlighted(false);
            painter.repaint();
        }

        //highlight present node if it is not already highlighted
        if(hoverNode != null && !hoverNode.isHighlighted()){
            hoverNode.setHighlighted(true);
            painter.repaint();
        }

        //prevents highlighting an edge when some node is currently highlighted
        if(hoverNode != null && hoverEdge != null){
            hoverEdge.setHighlighted(false);
            painter.repaint();
        }

        if(hoverNode == null){
            Edge prevEdge = hoverEdge;
            hoverEdge = getEdgeFromPoint(movePt);

            if (prevEdge != null && !prevEdge.equals(hoverEdge)) {
                prevEdge.setHighlighted(false);
                painter.repaint();
            }

            if (hoverEdge != null && !hoverEdge.isHighlighted()) {
                hoverEdge.setHighlighted(true);
                painter.repaint();
            }
        }
    }
    
    public void onMouseDragged(MouseEvent event) {

        Point2D delta = new Point2D( event.getX() - currentMousePt.getX(), event.getY() - currentMousePt.getY());
        Point2D mouseDragPt = new Point2D(event.getX(), event.getY());
        currentMousePt = mouseDragPt;

        if(inputScope.isEditLocked()){
            camera.translate(delta);
            currentMousePt = new Point2D(event.getX(), event.getY());
            painter.repaint();
            return;
        }

        if(mouseDragPt.distance(mousePressPt) > 10)
            dragging = true;

        if(!inputScope.isConnecting()) {
            if (edgeStartNode != null && getNodeFromPoint(mouseDragPt) != edgeStartNode){
                inputScope.setConnecting(true);
            }
        }

        if(event.getButton() == MouseButton.PRIMARY){

            if((pressedKeys.contains(KeyCode.SHIFT) || dragging) && !draggingNode){
                camera.translate(delta);
                currentMousePt = new Point2D(event.getX(),event.getY());
            }
            else if(clickedNode != null && !pressedKeys.contains(KeyCode.CONTROL)) {
                if (!clickedNode.isSelected()) {
                    GM.updateNodePosition(clickedNode, delta.multiply(1/camera.getZoom()));
                }
                else if(pressedKeys.contains(KeyCode.A)){
                    int clockwise = delta.getY() > 0? 1 : -1;
                    for (Node n : GM.getSelectedNodes()) {
                        GM.rotateNode(n, clickedNode.getPos(), clockwise * 0.02);
                    }
                }
                else {
                    for (Node n : GM.getSelectedNodes()) {
                        GM.updateNodePosition(n, delta.multiply(1/camera.getZoom()));
                    }
                }
                currentMousePt = new Point2D(event.getX(), event.getY());
            }
        }
        else if (event.getButton() == MouseButton.SECONDARY) {
            if(clickedNode == null && clickedEdge == null) {   // select in rectangle
                if(mouseDragPt.distance(mousePressPt) > 10){
                    inputScope.setSelecting(true);
                    currentMousePt = new Point2D(event.getX(), event.getY());
                    Rectangle2D selectRect = setSelectRectangle(currentMousePt);
                    inputScope.setSelectRect(selectRect);
                    selectNodesInRect(selectRect);
                }
            }
            else{ //dummy edge
                if(inputScope.isConnecting()) {
                    inputScope.setDummyEdgeEnd(camera.inverse(new Point2D((int) event.getX(), (int) event.getY())));
                    Point2D mousePoint  = new Point2D((int) event.getX(), (int) event.getY());
                    if (getNodeFromPoint(mousePoint) != null) {
                        if(edgeEndNode != null){
                            edgeEndNode.setHighlighted(false);
                        }
                        edgeEndNode = getNodeFromPoint(mousePoint);
                        inputScope.setDummyEdgeEnd(edgeEndNode.getPos());
                        edgeEndNode.setHighlighted(true);

                    } else {
                        if(edgeEndNode != null){
                            edgeEndNode.setHighlighted(false);
                            edgeEndNode = null;
                        }
                    }
                }
            }
        }
        painter.repaint();
    }

    public void onMouseScroll(ScrollEvent event){
        Point2D point = new Point2D(event.getX(), event.getY());

        if(event.getDeltaY()==0.0){
            return;
        }

        if(event.getDeltaY()>0) {
            camera.zoomIn(1.08, point);
        }
        else {
            camera.zoomIn(0.92, point);
        }
        painter.repaint();
    }

    public void onMouseExited(MouseEvent event) {
        Graph graph = graphScope.getGraphManager().getGraph();
        for (Node node : graph.getNodes()) {
            if(node.isHighlighted()){
                node.setHighlighted(false);
                break;
            }
        }
        for (Edge edge : graph.getEdges()) {
            if(edge.isHighlighted()){
                edge.setHighlighted(false);
                break;
            }
        }
        painter.repaint();
    }


    //-----------------------------------------------------
    //------------------ SECONDARY FUNCTIONS --------------
    //-----------------------------------------------------


    public Node getNodeFromPoint(Point2D point){
        double nodeSize = graphScope.getNodeSize();
        Point2D invPoint = camera.inverse(point);
        for (Node n : GM.getGraph().getNodes()) {
            if (n.getPos().distance(invPoint) < nodeSize/2) {
                return n;
            }
        }
        return null;
    }

    private Edge getEdgeFromPoint(Point2D pt){
        double threshold = graphScope.getNodeSize() * graphScope.getEdgeWidth();
        for(Edge edge : GM.getGraph().getEdges()){
            if(isPointNearEdge(camera.inverse(pt), edge, threshold/2)) {
                if (distK(camera.inverse(pt), edge.getN1(), edge.getN2()) < threshold) {
                    return edge;
                }
            }
        }
        return null;
    }

    public void selectNodesInRect(Rectangle2D rect){
        for(Node n: GM.getGraph().getNodes()){
            n.setSelected(rect.contains(camera.transform(n.getPos())));
        }
        for(Edge e: GM.getGraph().getEdges()){
            e.setMarked(e.getN1().isSelected() && e.getN2().isSelected());
        }
    }

    private void reverseSelection(Node n){
        if(n.isSelected()){
            n.setSelected(false);
            GM.getSelectedNodes().remove(n);
        }else{
            n.setSelected(true);
            GM.getSelectedNodes().add(n);
        }
        Graph g = GM.getGraph();
        for(Node m : n.getNeighbours()){
            Edge e = g.getEdge(n, m);
            e.setMarked(e.getN1().isSelected() && e.getN2().isSelected());
        }
    }

    private void reverseSelection(Edge e){
        if(e.isSelected()){
            e.setSelected(false);
            selectedEdges.remove(e);
        }else{
            e.setSelected(true);
            selectedEdges.add(e);
        }
    }

    public void hidePopOver(){
        if(popOver != null){
            popOver.hide();
        }
    }

    public void clearSelection(){
        for(Edge edge : selectedEdges){
            edge.setSelected(false);
            //edge.setHighlighted(false);
            //edge.setMarked(false);
        }
        for(Edge edge : GM.getGraph().getEdges()){
            edge.setMarked(false);
        }
        selectedEdges.clear();
        for(Node node : GM.getSelectedNodes()){
            node.setSelected(false);
        }
        GM.getSelectedNodes().clear();
        selectedEdges.clear();
    }

    private Rectangle2D setSelectRectangle(Point2D mousePt){
        double rectX;
        double rectY;
        double rectW;
        double rectH;

        if(mousePt.getX() >= inputScope.getRectStartX()){
            rectX = inputScope.getRectStartX();
            rectW = mousePt.getX()- inputScope.getRectStartX();
        }
        else{
            rectX = mousePt.getX();
            rectW = inputScope.getRectStartX() - mousePt.getX();
        }
        if(mousePt.getY() >= inputScope.getRectStartY()){
            rectY = inputScope.getRectStartY();
            rectH = mousePt.getY()- inputScope.getRectStartY();
        }
        else{
            rectY = mousePt.getY();
            rectH = inputScope.getRectStartY() - mousePt.getY();
        }
        return new Rectangle2D(rectX, rectY, rectW, rectH);
    }

    double distK(Point2D p, Node n, Node m){
        double a = m.getPos().getY() - n.getPos().getY();
        double b = n.getPos().getX() - m.getPos().getX();
        double c = n.getPos().getY() * m.getPos().getX() - m.getPos().getY() * n.getPos().getX();

        double x = p.getX();
        double y = p.getY();

        return Math.abs(a * x + b * y + c)/Math.sqrt(a*a + b*b);
    }

    boolean isPointNearEdge(Point2D p, Edge edge, double tol){
        Node n = edge.getN1();
        Node m = edge.getN2();
        double x1 = Math.min(n.getPos().getX(), m.getPos().getX());
        double x2 = Math.max(n.getPos().getX(), m.getPos().getX());
        double y1 = Math.min(n.getPos().getY(), m.getPos().getY());
        double y2 = Math.max(n.getPos().getY(), m.getPos().getY());
        return p.getX() >= x1 - tol && p.getX() <= x2 + tol && p.getY() >= y1 - tol && p.getY() <= y2 + tol;
    }


}