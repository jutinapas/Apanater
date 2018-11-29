package Controllers;

import Models.Room;
import Models.SqlConnection;
import Models.TypeRoom;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.io.IOException;
import java.util.*;

public class Feature1Page1Controller {

    @FXML
    private GridPane gridPane;

    @FXML
    private Button feature1Btn;
    @FXML
    private Button feature2Btn;
    @FXML
    private Button feature4Btn;
    @FXML
    private Button feature5Btn;

    @FXML
    private Button monthlyBtn;
    @FXML
    private Button dailyBtn;

    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private Spinner<Integer> numMonthField;
    @FXML
    private ComboBox<TypeRoom> roomTypeBox;
    @FXML
    private ComboBox<Integer> floorBox;

    @FXML
    private Button searchBtn;

    @FXML
    private Label toDateLabel;
    @FXML
    private Label numMonthLabel;
    @FXML
    private Label errorLabel;

    @FXML
    private TableView<RoomRecord> tableView;
    @FXML
    private TableColumn<RoomRecord, String> roomNameCol;
    @FXML
    private TableColumn<RoomRecord, String> roomTypeCol;
    @FXML
    private TableColumn<RoomRecord, String> floorCol;
    @FXML
    private TableColumn<RoomRecord, RoomRecord> buttonCol;

    private ObservableList<TypeRoom> typeRooms;
    private ObservableList<Integer> floors;

    private ObservableList<RoomRecord> results =

            FXCollections.observableArrayList(
                    new RoomRecord("room1", "1", "1"),
                    new RoomRecord("room2", "2", "2")
            );

    private final int MONTHLY = 0;
    private final int DAILY = 1;
    private int reserveType;

    public static class RoomRecord {

        private final SimpleStringProperty room_name;
        private final SimpleStringProperty type_room;
        private final SimpleStringProperty floor;

        private RoomRecord(String room_name, String type_room, String floor) {
            this.room_name = new SimpleStringProperty(room_name);
            this.type_room = new SimpleStringProperty(type_room);
            this.floor = new SimpleStringProperty(floor);
        }

        public String getRoom_name() {
            return room_name.get();
        }

        public SimpleStringProperty room_nameProperty() {
            return room_name;
        }

        public void setRoom_name(String room_name) {
            this.room_name.set(room_name);
        }

        public String getType_room() {
            return type_room.get();
        }

        public SimpleStringProperty type_roomProperty() {
            return type_room;
        }

        public void setType_room(String type_room) {
            this.type_room.set(type_room);
        }

        public String getFloor() {
            return floor.get();
        }

        public SimpleStringProperty floorProperty() {
            return floor;
        }

        public void setFloor(String floor) {
            this.floor.set(floor);
        }

    }

    @FXML
    private void initialize() {

        // Setup
        reserveType = MONTHLY;

        // Spinner
        SpinnerValueFactory factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE);
        numMonthField.setValueFactory(factory);
        numMonthField.setEditable(true);
        numMonthField.getValueFactory().setValue(1);
        TextFormatter formatter = new TextFormatter(factory.getConverter(), factory.getValue());
        numMonthField.getEditor().setTextFormatter(formatter);
        factory.valueProperty().bindBidirectional(formatter.valueProperty());

        // ComboBox
        typeRooms = FXCollections.observableArrayList();
        typeRooms.add(new TypeRoom(0, "ทุกประเภท", 0, 0, "active"));
        typeRooms.addAll(SqlConnection.getSqlConnection().selectAllTypeRoom());
        Callback<ListView<TypeRoom>, ListCell<TypeRoom>> typeFactory = lv -> new ListCell<TypeRoom>() {

            @Override
            protected void updateItem(TypeRoom typeRoom, boolean empty) {
                super.updateItem(typeRoom, empty);
                setText(empty ? "" : typeRoom.getTypeRoom());
            }

        };
        roomTypeBox.setCellFactory(typeFactory);
        roomTypeBox.setButtonCell(typeFactory.call(null));
        roomTypeBox.setItems(typeRooms);

        SortedSet<Integer> temp = new TreeSet<>();
        for (Room room : SqlConnection.getSqlConnection().selectAllRoom()) {
            temp.add(room.getFloor());
        }
        ObservableList list = FXCollections.observableArrayList();
        list.add("ทุกชั้น");
        list.addAll(temp);
        floorBox.setItems(list);

        // TableView
        tableView.setEditable(true);

        roomNameCol.setCellValueFactory(new PropertyValueFactory<RoomRecord, String>("room_name"));
        roomTypeCol.setCellValueFactory(new PropertyValueFactory<RoomRecord, String>("type_room"));
        floorCol.setCellValueFactory(new PropertyValueFactory<RoomRecord, String>("floor"));

        buttonCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RoomRecord, RoomRecord>, ObservableValue<RoomRecord>>() {
            @Override
            public ObservableValue<RoomRecord> call(TableColumn.CellDataFeatures<RoomRecord, RoomRecord> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        buttonCol.setCellFactory(new Callback<TableColumn<RoomRecord, RoomRecord>, TableCell<RoomRecord, RoomRecord>>() {
            @Override
            public TableCell<RoomRecord, RoomRecord> call(TableColumn<RoomRecord, RoomRecord> buttonCol) {
                return new TableCell<RoomRecord, RoomRecord>() {

                    final Button descButton = new Button();
                    final Button reserveButton = new Button();
                    ImageView view = new ImageView();

                    {
                        descButton.setMinWidth(150);
                        descButton.setMinHeight(50);
                        reserveButton.setMinWidth(150);
                        reserveButton.setMinHeight(50);
                        view.setImage(new Image("images/rounded-add-button.png"));
                        view.setFitHeight(25);
                        view.setFitWidth(25);
                        reserveButton.setGraphic(view);
                    }

                    @Override
                    public void updateItem(final RoomRecord room, boolean empty) {
                        super.updateItem(room, empty);
                        if (room != null) {

                            descButton.setText("ดูรายละเอียด");
                            descButton.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    try {
                                        GridPane pane = FXMLLoader.load(getClass().getResource("/fxml/Feature1Page2.fxml"));
                                        gridPane.getChildren().setAll(pane);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            reserveButton.setText("จองเลย");
                            reserveButton.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    try {
                                        GridPane pane = FXMLLoader.load(getClass().getResource("/fxml/Feature1Page3.fxml"));
                                        gridPane.getChildren().setAll(pane);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            GridPane gridPane = new GridPane();
                            gridPane.setMinSize(325, 75);
                            gridPane.setPadding(new Insets(5, 5, 5, 5));
                            gridPane.setVgap(5);
                            gridPane.setHgap(12.5);
                            gridPane.setAlignment(Pos.CENTER);

                            gridPane.add(descButton, 0, 0);
                            gridPane.add(reserveButton, 1, 0);

                            setGraphic(gridPane);

                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });

        update();

    }

    @FXML
    void update() {
        tableView.setItems(results);
    }

    @FXML
    void handleFeature1Btn(ActionEvent event) throws IOException {
        GridPane pane = FXMLLoader.load(getClass().getResource("/fxml/Feature1Page1.fxml"));
        gridPane.getChildren().setAll(pane);
    }

    @FXML
    void handleFeature2Btn(ActionEvent event) throws IOException {

    }

    @FXML
    void handleFeature4Btn(ActionEvent event) throws IOException {
        GridPane pane = FXMLLoader.load(getClass().getResource("/fxml/PageRoomManagementMain.fxml"));
        gridPane.getChildren().setAll(pane);
    }

    @FXML
    void handleFeature5Btn(ActionEvent event) throws IOException {
        GridPane pane = FXMLLoader.load(getClass().getResource("/fxml/ManageApartmentAndEditPage.fxml"));
        gridPane.getChildren().setAll(pane);
    }

    @FXML
    void handleDailyBtn(ActionEvent event) {
        if (reserveType == MONTHLY) {
            swapDisable(numMonthField);
            swapDisable(numMonthLabel);
            numMonthField.getValueFactory().setValue(1);
            swapDisable(toDatePicker);
            toDatePicker.setValue(null);
            swapDisable(toDateLabel);

            reserveType = DAILY;
        }
    }

    @FXML
    void handleMonthlyBtn(ActionEvent event) {
        if (reserveType == DAILY) {
            swapDisable(toDatePicker);
            toDatePicker.setValue(null);
            swapDisable(toDateLabel);
            swapDisable(numMonthField);
            numMonthField.getValueFactory().setValue(1);
            swapDisable(numMonthLabel);

            reserveType = MONTHLY;
        }
    }

    @FXML
    private void swapDisable(Node node) {
        if (node.isDisable()) {
            node.setDisable(false);
            node.setVisible(true);
        }
        else {
            node.setDisable(true);
            node.setVisible(false);
        }
    }

    @FXML
    void handleSearchBtn(ActionEvent event) throws IOException {
        if (reserveType == MONTHLY &&
        toDatePicker.getValue() != null) {
            if (roomTypeBox.getValue().equals("ทุกประเภท") && floorBox.getValue().equals("ทุกชั้น")) {
                // results = FXCollections.observableArrayList(SqlConnection.getSqlConnection().selectIDRoomThatReservationNotInRange(toDatePicker.getValue(), toDatePicker.getValue().plusMonths(numMonthField.getValue())));
            }
        } else if (reserveType == DAILY) {

        }
    }

}
