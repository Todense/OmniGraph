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

    private Point2D mousePt;

    private Node edgeStartNode;
    private Node edgeEndNode;

    private Node clickedNode; //clicked node
    private Edge clickedEdge; //clicked edge

    private Node hoverNode; //node under mouse
    private Edge hoverEdge; //edge under mouse


    private final ArrayList<Node> selectedNodes = new ArrayList<>();
    private final ArrayList<Edge> selectedEdges = new ArrayList<>();

    private Set<KeyCode> pressedKeys;


    public MouseHandler(Camera camera, InputScope inputScope, Painter painter, GraphScope graphScope, PopOverManager popOverManager, Set<KeyCode> pressedKeys){
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

    public void onMousePressed(MouseEvent e){

        mousePt = new Point2D(e.getX(), e.getY());

        if(inputScope.isEditLocked()) return;

        hidePopOver();

        this.clickedNode = getNodeFromPoint(mousePt);
        this.clickedEdge = getEdgeFromPoint(mousePt);

        if(e.getButton() == MouseButton.PRIMARY) {  // LEFT MOUSE BUTTON
            if(!pressedKeys.contains(KeyCode.SHIFT)){
                if(clickedNode == null && clickedEdge == null){  // click on background
                    GM.addNode(camera.inverse(mousePt));
                }
                else if(pressedKeys.contains(KeyCode.CONTROL)){
                    if(clickedNode != null){
                        reverseSelection(clickedNode);
                    }
                    else{
                        reverseSelection(clickedEdge);
                    }
                }
            }
        }

        if(e.getButton() == MouseButton.SECONDARY) {   //RIGHT MOUSE BUTTON
            inputScope.setSelecting(false);
            if (clickedNode == null && clickedEdge == null) {  //start selection rectangle
                clearSelection();
                inputScope.setRectStartX(mousePt.getX());
                inputScope.setRectStartY(mousePt.getY());
            }
            else if (clickedNode != null){           //start dummy edge
                edgeStartNode = clickedNode;
                inputScope.setDummyEdgeStart(edgeStartNode.getPos());
                inputScope.setDummyEdgeEnd(edgeStartNode.getPos());
            }
        }

        if(e.getButton() == MouseButton.MIDDLE) {   // MIDDLE MOUSE BUTTON
            if(clickedNode != null){
                GM.removeNode(clickedNode);
            }
            else if(clickedEdge != null){
                GM.removeEdge(clickedEdge);
            }
        }
        painter.repaint();
    }

    public void onMouseReleased(MouseEvent e){

        if(inputScope.isEditLocked()) return;

        Point2D releasePt = new Point2D(e.getX(), e.getY());

        Node releaseNode = getNodeFromPoint(releasePt);
        Edge releaseEdge = getEdgeFromPoint(releasePt);

        if(e.getButton() == MouseButton.SECONDARY) {
            if(inputScope.isConnecting()){
                if (releaseNode != null) {             // add/remove edge
                    if (GM.noEdgeBetween(releaseNode, edgeStartNode)) {
                        GM.addEdge(releaseNode, edgeStartNode);
                    } else {
                        GM.removeEdge(releaseNode, edgeStartNode);
                    }
                    releaseNode.setHighlighted(false);
                    edgeStartNode.setHighlighted(false);
                }
            }

            else if(!inputScope.isSelecting()){
                if (releaseNode != null) {         //node popover
                    if (!releaseNode.isSelected()) {
                        clearSelection();
                        releaseNode.setSelected(true);
                        selectedNodes.add(releaseNode);
                    }
                    popOver = popOverManager.createNodePopOver(graphScope.getGraphManager(), selectedNodes, e.getScreenX(), e.getScreenY());
                } else if (releaseEdge != null) {  //edge popover
                    if (!releaseEdge.isSelected()) {
                        clearSelection();
                        releaseEdge.setSelected(true);
                        selectedEdges.add(releaseEdge);
                    }
                    popOver = popOverManager.createEdgePopOver(graphScope.getGraphManager(), selectedEdges, e.getScreenX(), e.getScreenY());
                }
            }

            inputScope.setConnecting(false);

            if(inputScope.isSelecting()){
                inputScope.setSelecting(false);
                selectedNodes.addAll(GM.getSelectedNodes());
            }
            edgeStartNode = null;
        }

        painter.repaint();
    }


    public void onMouseMoved(MouseEvent e){

        if(inputScope.isEditLocked()) return;

        Point2D movePt = new Point2D(e.getX(), e.getY());

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
    
    public void onMouseDragged(MouseEvent e) {

        Point2D delta = new Point2D( e.getX() - mousePt.getX(), e.getY() - mousePt.getY());

        if(inputScope.isEditLocked()){
            camera.translate(delta);
            mousePt = new Point2D(e.getX(),e.getY());
            painter.repaint();
            return;
        }

        Point2D mouseDragPoint = new Point2D(e.getX(), e.getY());

        if(!inputScope.isConnecting()) {
            if (edgeStartNode != null && getNodeFromPoint(mouseDragPoint) != edgeStartNode){
                inputScope.setConnecting(true);
            }
        }

        if(e.getButton() == MouseButton.PRIMARY){

            if(pressedKeys.contains(KeyCode.SHIFT)){
                camera.translate(delta);
                mousePt = new Point2D(e.getX(),e.getY());
            }
            else if(clickedNode != null) {
                if (!clickedNode.isSelected()) {
                    GM.updateNodePosition(clickedNode, delta.multiply(1/ camera.getZoom()));
                }
                else {
                    for (Node n : selectedNodes) {
                        GM.updateNodePosition(n, delta.multiply(1/ camera.getZoom()));
                    }
                }
                mousePt = new Point2D(e.getX(), e.getY());
            }
        }
        else if (e.getButton() == MouseButton.SECONDARY) {
            if(clickedNode == null && clickedEdge == null) {   // select in rectangle
                inputScope.setSelecting(true);
                mousePt = new Point2D(e.getX(), e.getY());
                Rectangle2D selectRect = setSelectRectangle(mousePt);
                inputScope.setSelectRect(selectRect);
                selectNodesInRect(selectRect);
            }
            else{ //dummy edge
                if(inputScope.isConnecting()) {
                    inputScope.setDummyEdgeEnd(camera.inverse(new Point2D((int) e.getX(), (int) e.getY())));
                    Point2D mousePoint  = new Point2D((int) e.getX(), (int) e.getY());
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
    }



    private void reverseSelection(Node n){
        if(n.isSelected()){
            n.setSelected(false);
            selectedNodes.remove(n);
        }else{
            n.setSelected(true);
            selectedNodes.add(n);
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
        }
        selectedEdges.clear();
        for(Node node : selectedNodes){
            node.setSelected(false);
        }
        selectedNodes.clear();
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