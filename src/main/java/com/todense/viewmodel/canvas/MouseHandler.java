package com.todense.viewmodel.canvas;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.popover.PopOverManager;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.InputScope;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.input.*;
import org.controlsfx.control.PopOver;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class MouseHandler {

    private final GraphManager GM;
    private final GraphScope graphScope;
    private final Painter painter;
    private final Camera camera;
    private final InputScope inputScope;

    // camera movement trigger threshold
    private final int CAMERA_MOVE_TRIGGER_DISTANCE = 10;

    // key used for rotating subgraphs
    private final KeyCode NODE_ROTATION_KEY = KeyCode.N;

    PopOverManager popOverManager;

    private Point2D mousePressPt;
    private Point2D mousePressScreenPt;
    private Point2D currentMousePt;
    private Point2D mouseDragPt;

    // dummy edge represents temporary edge which user is currently creating by mouse drag
    private Node dummyEdgeStartNode;
    private Node dummyEdgeEndNode;

    private Node clickedNode; //clicked node
    private Edge clickedEdge; //clicked edge

    private Node hoverNode; //node under mouse
    private Edge hoverEdge; //edge under mouse

    private Node releaseNode; //node on mouse released
    private Edge releaseEdge; // edge on mouse released

    private boolean movingCamera = false;
    private boolean draggingNode = false;

    private final ArrayList<Edge> selectedEdges = new ArrayList<>();

    private final ObservableSet<KeyCode> pressedKeys;

    public MouseHandler(Camera camera,
                        InputScope inputScope,
                        Painter painter,
                        GraphScope graphScope,
                        PopOverManager popOverManager){
        this.camera = camera;
        this.inputScope = inputScope;
        this.painter = painter;
        this.graphScope = graphScope;
        this.GM = graphScope.getGraphManager();
        this.popOverManager = popOverManager;
        this.pressedKeys = inputScope.getPressedKeys();
    }

    //-----------------------------------------------------
    //-------------------- MOUSE EVENTS -------------------
    //-----------------------------------------------------

    public void onMousePressed(MouseEvent event){

        mousePressPt = new Point2D(event.getX(), event.getY());
        mousePressScreenPt = new Point2D(event.getScreenX(), event.getScreenY());

        if(inputScope.isEditLocked()) return;

        hidePopOver();

        setMovingCamera(false);

        this.clickedNode = getNodeFromPoint(mousePressPt);
        this.clickedEdge = getEdgeFromPoint(mousePressPt);

        if(event.getButton() == MouseButton.PRIMARY){ //Left
            handlePrimaryButtonPressed();
        }
        else if(event.getButton() == MouseButton.SECONDARY) { //Right
            handleSecondaryButtonPressed();
        }
        else if(event.getButton() == MouseButton.MIDDLE) { // Middle
            handleMiddleButtonPressed();
        }

        painter.repaint();
    }

    public void onMouseClicked(MouseEvent event) {

        if(inputScope.isEditLocked() || inputScope.isEraseModeOn())
            return;

        if(event.getButton() == MouseButton.PRIMARY) {  // LEFT MOUSE BUTTON
            handlePrimaryButtonClicked(event);
        }
    }

    public void onMouseReleased(MouseEvent event){

        if(inputScope.isEditLocked())
            return;

        // save node/edge on mouse release position
        Point2D releasePt = new Point2D(event.getX(), event.getY());
        this.releaseNode = getNodeFromPoint(releasePt);
        this.releaseEdge = getEdgeFromPoint(releasePt);

        // node (or nodes) is no longer being dragged
        if(clickedNode != null){
            clickedNode.setDragged(false);
        }
        GM.getSelectedNodes().forEach(n -> n.setDragged(false));
        draggingNode = false;

        if(event.getButton() == MouseButton.SECONDARY) { // handle right button
            handleSecondaryButtonReleased(event);
        }

        // clear dummy edge start nodes
        inputScope.getDummyEdgeStartNodes().clear();

        painter.repaint();
    }

    public void onMouseMoved(MouseEvent event){

        currentMousePt = new Point2D(event.getX(), event.getY());

        // update mouse position property
        inputScope.mousePositionProperty().set(camera.inverse(currentMousePt));

        if(inputScope.isEditLocked())
            return;

        // update current hover node
        Node prevNode = hoverNode;
        hoverNode = getNodeFromPoint(currentMousePt);

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

        if(hoverNode == null){ // if mouse was not above node, check similarly for edge
            // update hover edge
            Edge prevEdge = hoverEdge;
            hoverEdge = getEdgeFromPoint(currentMousePt);

            //unhighlight previous edge if it is not equal to the present
            if (prevEdge != null && !prevEdge.equals(hoverEdge)) {
                prevEdge.setHighlighted(false);
                painter.repaint();
            }

            //highlight present edge if it is not already highlighted
            if (hoverEdge != null && !hoverEdge.isHighlighted()) {
                hoverEdge.setHighlighted(true);
                painter.repaint();
            }
        }

        if(!inputScope.isEraseModeOn()){ // change cursor
            if(hoverNode == null){
                inputScope.setCanvasCursor(Cursor.DEFAULT);
            }else{
                inputScope.setCanvasCursor(Cursor.OPEN_HAND);
            }
        }
    }

    public void onMouseDragged(MouseEvent event) {

        Point2D delta = new Point2D(
                event.getX() - currentMousePt.getX(),
                event.getY() - currentMousePt.getY()
        );

        this.mouseDragPt = new Point2D(event.getX(), event.getY());

        // update mouse position property
        inputScope.mousePositionProperty().set(camera.inverse(mouseDragPt));
        currentMousePt = mouseDragPt;

        if(inputScope.isEditLocked()){ // if edit it locked, user can only move camera
            camera.translate(delta);
            painter.repaint();
            return;
        }

        if(event.getButton() == MouseButton.PRIMARY){
            handlePrimaryButtonDragged(event, delta);
        }
        else if (event.getButton() == MouseButton.SECONDARY) {
            handleSecondaryButtonDragged(event);
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

    //-----------------------------------------------------
    //----------- SINGLE MOUSE BUTTON HANDLING ------------
    //-----------------------------------------------------

    private void handlePrimaryButtonPressed(){

        // handle node/edge deletion (when mouse was not moved)
        if(inputScope.isEraseModeOn()){
            // if node was clicked
            if(clickedNode != null){
                // delete all selected
                if(clickedNode.isSelected()){
                    for(Node n: GM.getSelectedNodes()){
                        GM.performOperation(GM.nodeDeletionOperation(n));
                    }
                    GM.getSelectedNodes().clear();
                }
                // delete only clicked node
                else{
                    GM.performOperation(GM.nodeDeletionOperation(clickedNode));
                }
                // clickedNode was deleted - set it to null
                clickedNode = null;
            }
            // if edge was clicked
            else if(clickedEdge != null){
                GM.performOperation(GM.edgeDeletionOperation(clickedEdge));
                clickedEdge = null;
            }
        }

        // indicate that node is being dragged
        if(clickedNode != null){
            draggingNode = true;
        }
    }

    private void handleSecondaryButtonPressed(){

        // reset selection flag
        inputScope.setSelecting(false);

        // start selection rectangle if mouse wasn't under node/edge
        if (clickedNode == null && clickedEdge == null) {
            clearSelection();
            inputScope.setRectStartX(mousePressPt.getX());
            inputScope.setRectStartY(mousePressPt.getY());
        }
        // start dummy edge if node was clicked
        else if (clickedNode != null){
            dummyEdgeStartNode = clickedNode;
            // multi-node dummy edge
            if(clickedNode.isSelected()){
                inputScope.getDummyEdgeStartNodes().addAll(GM.getSelectedNodes());
            }
            // single-node dummy edge
            else {
                inputScope.getDummyEdgeStartNodes().add(clickedNode);
            }
            // set dummy edge end position
            inputScope.setDummyEdgeEnd(dummyEdgeStartNode.getPos());
        }
    }

    private void handleMiddleButtonPressed(){
        // delete clicked node
        if(clickedNode != null){
            GM.performOperation(GM.nodeDeletionOperation(clickedNode));
        }
        // delete clicked edge
        else if(clickedEdge != null){
            GM.performOperation(GM.edgeDeletionOperation(clickedEdge));
        }
    }

    private void handlePrimaryButtonClicked(MouseEvent event){
        if(!movingCamera){ // ignore click effect when camera has moved
            if(clickedNode == null && clickedEdge == null){  // click on background
                // paste subgraph
                if(pressedKeys.contains(KeyCode.CONTROL) && pressedKeys.contains(KeyCode.V)){
                    Point2D transPt = camera.inverse(new Point2D(event.getX(), event.getY()));
                    GM.addSubgraph(transPt);
                }
                // add node
                else{
                    GM.performOperation(GM.nodeAdditionOperation(camera.inverse(mousePressPt)));
                }
            }
            else { // click on node/edge
                if(pressedKeys.contains(KeyCode.CONTROL)){ // reverse selection on node/edge
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

    private void handleSecondaryButtonReleased(MouseEvent event){
        if(inputScope.isConnecting()){ // connecting/disconnecting edge
            if (releaseNode != null) {
                for(Node n : inputScope.getDummyEdgeStartNodes()){ // add/remove edge
                    if(n.equals(releaseNode))
                        continue;
                    if (!GM.isEdgeBetween(releaseNode, n)) {
                        GM.performOperation(GM.edgeAdditionOperation(releaseNode, n));
                    } else {
                        GM.performOperation(GM.edgeDeletionOperation(releaseNode, n));
                    }
                    n.setHighlighted(false);
                }
                releaseNode.setHighlighted(false);
            }
            inputScope.getDummyEdgeStartNodes().clear();
            inputScope.setConnecting(false);
        }
        else if(!inputScope.isSelecting()){ // not connecting & not selecting
            if (releaseNode != null) { //node popover
                if (!releaseNode.isSelected()) { // if node wasn't selected, set is as the only node selected
                    clearSelection();
                    releaseNode.setSelected(true);
                    GM.getSelectedNodes().add(releaseNode);
                }
                //show node popover
                popOverManager.showNodePopOver(releaseNode.getPos(),
                        GM.getSelectedNodes(), event.getScreenX(), event.getScreenY());
            } else if (releaseEdge != null) {  //edge popover
                if (!releaseEdge.isSelected()) { // if edge wasn't selected, set is as the only edge selected
                    clearSelection();
                    releaseEdge.setSelected(true);
                    selectedEdges.add(releaseEdge);
                }
                //show edge popover
                popOverManager.showEdgePopOver(graphScope.getGraphManager(),
                        selectedEdges, event.getScreenX(), event.getScreenY());
            } else{ // mouse released over background
                Point2D transPt = camera.inverse(new Point2D(event.getX(), event.getY()));
                // show background popover
                popOverManager.showBackgroundPopOver(event.getScreenX(), event.getScreenY(), transPt);
            }
        }

        if(inputScope.isSelecting()){ // selection finish
            inputScope.setSelecting(false);
            GM.createSelectedNodesList();
            // mark all edges that are between selected nodes
            for(Edge edge : GM.getGraph().getEdges()){
                edge.setMarked(edge.getN1().isSelected() && edge.getN2().isSelected());
            }
            //show node pop over if selection is not empty (maybe delete this?)
            if(!GM.getSelectedNodes().isEmpty()){
                Point2D showPt = mousePressScreenPt.getX() > event.getScreenX()
                        ? mousePressScreenPt
                        : new Point2D(event.getScreenX(), event.getScreenY());

                popOverManager.showNodePopOver(
                        mousePressScreenPt,
                        GM.getSelectedNodes(), showPt.getX(), showPt.getY()
                );
            }
        }
        dummyEdgeStartNode = null;
    }

    private void handlePrimaryButtonDragged(MouseEvent event, Point2D delta){
        //check if camera move has been triggered
        if(mouseDragPt.distance(mousePressPt) > CAMERA_MOVE_TRIGGER_DISTANCE){
            setMovingCamera(true);
        }

        if(inputScope.isEraseModeOn()){ // handle erase mode deletions
            Node node = getNodeFromPoint(mouseDragPt);
            if (node != null) {
                GM.performOperation(GM.nodeDeletionOperation(node));
                painter.repaint();
            } else {
                Edge edge = getEdgeFromPoint(mouseDragPt);
                if (edge != null) {
                    GM.performOperation(GM.edgeDeletionOperation(edge));
                    painter.repaint();
                }
            }
        }
        else if(movingCamera && !draggingNode){ // move camera
            camera.translate(delta);
            currentMousePt = new Point2D(event.getX(),event.getY());
        }
        else if(clickedNode != null) {
            if (!clickedNode.isSelected()) { // move single node
                var newPos = clickedNode.getPos().add(delta.multiply(1/camera.getZoom()));
                GM.getGraph().setNodePosition(clickedNode, newPos); // update node position
                clickedNode.setDragged(true);
            }
            else{ // clicked node is selected
                if(pressedKeys.contains(NODE_ROTATION_KEY)){ // rotate nodes by delta y
                    int clockwise = delta.getY() > 0? 1 : -1;
                    for (Node n : GM.getSelectedNodes()) {
                        GM.rotateNode(n, clickedNode.getPos(), clockwise * 0.02);
                    }
                }
                else { // move selected nodes
                    for (Node n : GM.getSelectedNodes()) {
                        var newPos = n.getPos().add(delta.multiply(1/camera.getZoom()));
                        GM.getGraph().setNodePosition(n, newPos);
                        n.setDragged(true);
                    }
                }
            }
        }
    }

    private void handleSecondaryButtonDragged(MouseEvent event){
        if(!inputScope.isConnecting()) {
            // if dummy edge start has been set and mouse has moved outside edge start node, start connecting
            if (dummyEdgeStartNode != null && getNodeFromPoint(mouseDragPt) != dummyEdgeStartNode){
                inputScope.setConnecting(true);
            }
        }
        if(clickedNode == null && clickedEdge == null) {
            // start node selection rectangle, if mouse has been dragged more than threshold
            if(mouseDragPt.distance(mousePressPt) > CAMERA_MOVE_TRIGGER_DISTANCE){
                inputScope.setSelecting(true);
                currentMousePt = new Point2D(event.getX(), event.getY());
                Rectangle2D selectionRectangle = setSelectionRectangle(currentMousePt);
                inputScope.setSelectRect(selectionRectangle);
                selectNodesInRect(selectionRectangle);
            }
        }
        else{ //dummy edge
            if(inputScope.isConnecting()) {
                // update dummy edge end position
                inputScope.setDummyEdgeEnd(camera.inverse(new Point2D((int) event.getX(), (int) event.getY())));
                Node currentHoverNode = getNodeFromPoint(mouseDragPt);

                if (currentHoverNode != null) { // mouse is above node
                    // unhighlight previous dummy edge end node
                    if(dummyEdgeEndNode != null){
                        dummyEdgeEndNode.setHighlighted(false);
                    }

                    // set actual dummy edge end node, and highlight it
                    dummyEdgeEndNode = currentHoverNode;
                    inputScope.setDummyEdgeEnd(dummyEdgeEndNode.getPos());
                    dummyEdgeEndNode.setHighlighted(true);

                } else { // mouse is  not above node
                    if(dummyEdgeEndNode != null){ // reset dummy edge end node
                        dummyEdgeEndNode.setHighlighted(false);
                        dummyEdgeEndNode = null;
                    }
                }
            }
        }
    }

    //-----------------------------------------------------
    //------------------ SECONDARY FUNCTIONS --------------
    //-----------------------------------------------------


    private Node getNodeFromPoint(Point2D point){
        double nodeSize = graphScope.getNodeSize();
        Point2D invPoint = camera.inverse(point);
        try {
            for (Node n : GM.getGraph().getNodes()) {
                if(n == null)
                    continue;
                if (n.getPos().distance(invPoint) < nodeSize/2) {
                    return n;
                }
            }
        } catch (ConcurrentModificationException e) {
            return null;
        }
        return null;
    }

    private Edge getEdgeFromPoint(Point2D pt){
        double threshold = graphScope.getNodeSize() * graphScope.getEdgeWidth();
        Point2D invPt = camera.inverse(pt);
        try {
            for(Edge edge : GM.getGraph().getEdges()){
                boolean nearEdge = isPointNearEdge(invPt, edge, threshold/2);
                if(nearEdge) {
                    boolean found = distK(invPt, edge.getN1(), edge.getN2()) < threshold;
                    if (found) {
                        return edge;
                    }
                }
            }
        } catch (ConcurrentModificationException ignored) {
            return null;
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
        PopOver currentPopOver = popOverManager.getCurrentPopOver();
        if(currentPopOver != null){
            currentPopOver.hide();
        }
    }

    public void clearSelection(){
        synchronized (Graph.LOCK){
            for (Edge edge : selectedEdges) {
                edge.setSelected(false);
            }
            for (Edge edge : GM.getGraph().getEdges()) {
                edge.setMarked(false);
            }
            selectedEdges.clear();
            for (Node node : GM.getSelectedNodes()) {
                node.setSelected(false);
            }
            GM.getSelectedNodes().clear();
            selectedEdges.clear();
        }
    }

    private Rectangle2D setSelectionRectangle(Point2D mousePt){
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

    private void setMovingCamera(boolean b){
        this.movingCamera = b;
        if (!inputScope.isEraseModeOn() && !draggingNode){
            if(b){
                inputScope.setCanvasCursor(Cursor.MOVE);
            }
        }
    }


}