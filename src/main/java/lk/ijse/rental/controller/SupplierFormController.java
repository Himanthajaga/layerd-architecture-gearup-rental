package lk.ijse.rental.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lk.ijse.rental.bo.BOFactory;
import lk.ijse.rental.bo.custom.SupplierBO;
import lk.ijse.rental.dto.ReservationDTO;
import lk.ijse.rental.dto.SupplierDTO;
import lk.ijse.rental.entity.Supplier;
import lk.ijse.rental.qrGenerate.QrcodeForMachine;
import lk.ijse.rental.tdm.SupplierTm;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SupplierFormController {

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnDeleteSupplier;

    @FXML
    private JFXButton btnPaySupplier;

    @FXML
    private JFXButton btnSaveSupplier;

    @FXML
    private JFXButton btnUpdateSupplier;

    @FXML
    private TableColumn<?, ?> colSupplierAddress;

    @FXML
    private TableColumn<?, ?> colSupplierEmail;

    @FXML
    private TableColumn<?, ?> colSupplierId;

    @FXML
    private TableColumn<?, ?> colSupplierName;

    @FXML
    private TableColumn<?, ?> colSupplierTele;

    @FXML
    private AnchorPane paneholder;
    @FXML
    private TextField txtSupplierIdnew;
    @FXML
    private TableView<SupplierTm> tblSupplier;

    @FXML
    private TextField txtSupplierAdddress;

    @FXML
    private TextField txtSupplierEmail;
    @FXML
    private Label txtSupplierId;


    @FXML
    private TextField txtSupplierName;

    @FXML
    private TextField txtSupplierTele;

    private List<SupplierDTO> supplierList = new ArrayList<>();
    SupplierBO supplierBO = (SupplierBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.Supplier);
    private QrcodeForMachine qrcodeForUser = new QrcodeForMachine();

    public void initialize() throws SQLException, ClassNotFoundException {

        this.supplierList = getAllSuppliers();
        setCellValueFactory();
        loadSupplierTable();
        loadNextSupplierId();
        setListener();
    }

    private void setListener() {
        tblSupplier.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    SupplierDTO dto = new SupplierDTO(
                            newValue.getColSupplierId(),
                            newValue.getColSupplierName(),
                            newValue.getColSupplierAddress(),
                            newValue.getColSupplierTele(),
                            newValue.getColSupplierEmail()
                    );
                    setFields(dto);
                });
    }

    private void setFields(SupplierDTO dto) {
        txtSupplierIdnew.setText(dto.getS_id());
        txtSupplierName.setText(dto.getS_name());
        txtSupplierAdddress.setText(dto.getS_address());
        txtSupplierTele.setText(dto.getS_tel());
        txtSupplierEmail.setText(dto.getS_email());
    }

    private void loadNextSupplierId() throws SQLException, ClassNotFoundException {
        String lastSupplierId = supplierBO.getLastSupplierId();
        if (lastSupplierId != null) {
            int id = Integer.parseInt(lastSupplierId.replace("S", ""));
            id++;
            if (id < 10) {
                txtSupplierId.setText("S00" + id);
            } else if (id < 100) {
                txtSupplierId.setText("S0" + id);
            } else {
                txtSupplierId.setText("S" + id);
            }
        } else {
            txtSupplierId.setText("S001");
        }
    }

    @FXML
    void btnPaySupplierOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/payment_form.fxml"));
        Pane registePane = (Pane) fxmlLoader.load();
        paneholder.getChildren().clear();
        paneholder.getChildren().add(registePane);

    }

    private void setCellValueFactory() {
        colSupplierId.setCellValueFactory(new PropertyValueFactory<>("colSupplierId"));
        colSupplierName.setCellValueFactory(new PropertyValueFactory<>("colSupplierName"));
        colSupplierAddress.setCellValueFactory(new PropertyValueFactory<>("colSupplierAddress"));
        colSupplierTele.setCellValueFactory(new PropertyValueFactory<>("colSupplierTele"));
        colSupplierEmail.setCellValueFactory(new PropertyValueFactory<>("colSupplierEmail"));


    }

    private void loadSupplierTable() {

        ObservableList<SupplierTm> tmList = FXCollections.observableArrayList();

        for (SupplierDTO supplier : supplierList) {
            SupplierTm supplierTm = new SupplierTm(
                    supplier.getS_id(),
                    supplier.getS_name(),
                    supplier.getS_address(),
                    supplier.getS_tel(),
                    supplier.getS_email()


            );
            System.out.println("machineTm = " + supplierTm);
            tmList.add(supplierTm);
        }
        tblSupplier.setItems(tmList);
        SupplierTm selectedItem = (SupplierTm) tblSupplier.getSelectionModel().getSelectedItem();
        System.out.println("selectedItem = " + selectedItem);
    }


    private List<SupplierDTO> getAllSuppliers() {
        List<SupplierDTO> supplierList = null;
        try {
            supplierList = supplierBO.getAllSuppliers();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return supplierList;
    }

    @FXML
    void btnDeleteSupplierOnAction(ActionEvent event) {
        String id = txtSupplierIdnew.getText();

        try {
            boolean isDeleted = supplierBO.deleteSupplier(id);
            if (isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "customer deleted!").show();
                loadSupplierTable();
                clearFields();
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }




    @FXML
    void btnSaveSupplierOnAction(ActionEvent event) {
        String s_id = txtSupplierId.getText();
        String s_name = txtSupplierName.getText();
        String s_address = txtSupplierAdddress.getText();
        String s_tel = txtSupplierTele.getText();
        String s_email = txtSupplierEmail.getText();
        if (s_id.trim().length() == 0 || s_name.trim().length() == 0 || s_address.trim().length() == 0 || s_tel.trim().length() == 0 || s_email.trim().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Please fill all the fields").show();
            return;
        }

        Supplier supplier = new Supplier(s_id, s_name, s_address, s_tel,s_email);
        try {
                boolean isSaved = supplierBO.addSupplier(new SupplierDTO(supplier.getS_id(), supplier.getS_name(), supplier.getS_address(), supplier.getS_tel(),supplier.getS_email()));
                if (isSaved) {
                    qrcodeForUser.CreateQr(s_id);
                    supplierList.add(new SupplierDTO(supplier.getS_id(),supplier.getS_name(),supplier.getS_address(),supplier.getS_tel(),supplier.getS_email()));
                    loadSupplierTable();
                    clearFields();
                    loadNextSupplierId();
                }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
//    public boolean IsSaved(){
//        if (!Regex.setTextColor(TextField.ID,txtSupplierId)) return false;
//        if (!Regex.setTextColor(TextField.ADDRESS,txtSupplierAdddress)) return false;
//        return true;
//    }
    @FXML
    void txtSupplierAddressReleasedOnAction(KeyEvent event) {
        Pattern idPattern = Pattern.compile("^[a-zA-Z ]*$");
        if (!idPattern.matcher(txtSupplierAdddress.getText()).matches()) {
            addError(txtSupplierAdddress);

        }else{
            removeError(txtSupplierAdddress);
        }
    }

    private void removeError(TextField txtSupplierAdddress) {
        txtSupplierAdddress.setStyle("-fx-border-color: green; -fx-border-width: 5");
    }

    private void addError(TextField txtSupplierAdddress) {
        txtSupplierAdddress.setStyle("-fx-border-color: red; -fx-border-width: 5");
    }

//    @FXML
//    void txtSupplierIdReleasedOnAction(KeyEvent event) {
//        Pattern idPattern = Pattern.compile("^(S)[0-9]{1,}$");
//        if (!idPattern.matcher(txtSupplierId.getText()).matches()) {
//            addError(txtSupplierId);
//
//        }else{
//            removeError(txtSupplierId);
//        }
//    }
    @FXML
    void btnUpdateSupplierOnAction(ActionEvent event) {
        String id =txtSupplierIdnew.getText();
        String name = txtSupplierName.getText();
        String address = txtSupplierAdddress.getText();
        String tel = txtSupplierTele.getText();
        String email = txtSupplierEmail.getText();

        Supplier supplier = new Supplier(id, name, address, tel,email);
        System.out.println("awaa");

        try {
            boolean isUpdated = supplierBO.updateSupplier(new SupplierDTO(supplier.getS_id(), supplier.getS_name(), supplier.getS_address(), supplier.getS_tel(),supplier.getS_email()));
            System.out.println("awaaaaaaa");
            if (isUpdated) {
                new Alert(Alert.AlertType.CONFIRMATION, "customer updated!").show();
                loadSupplierTable();
                clearFields();
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }


    @FXML
    void btnClearCustomerOnAction(ActionEvent event) {
    clearFields();
    }

    private void clearFields() {
        txtSupplierId.setText("");
        txtSupplierName.clear();
        txtSupplierAdddress.clear();
        txtSupplierTele.clear();
    }


    public void txtSupplierNamereleasedonAction(KeyEvent keyEvent) {
        Pattern idPattern = Pattern.compile("^[a-zA-Z ]*$");
        if (!idPattern.matcher(txtSupplierName.getText()).matches()) {
            addError(txtSupplierName);

        }else{
            removeError(txtSupplierName);
        }

    }

    public void txtSupplierteleOnReleasedOnAction(KeyEvent keyEvent) {
        Pattern idPattern = Pattern.compile("^[0]{1}[7]{1}[01245678]{1}[0-9]{7}$");
        if (!idPattern.matcher(txtSupplierTele.getText()).matches())
        {
            addError(txtSupplierTele);
        }else{
            removeError(txtSupplierTele);

        }
    }

    public void txtSupplierEmailreleasedonAction(KeyEvent keyEvent) {
        Pattern idPattern = Pattern.compile("^([A-z])([A-z0-9.]){1,}[@]([A-z0-9]){1,10}[.]([A-z]){2,5}$");
        if (!idPattern.matcher(txtSupplierEmail.getText()).matches()) {
            addError(txtSupplierEmail);
        }else{
            removeError(txtSupplierEmail);
        }
    }

    public void supllierIdReleased(KeyEvent keyEvent) {
        Pattern idPattern = Pattern.compile("^(S)[0-9]{1,}$");
        if (!idPattern.matcher(txtSupplierIdnew.getText()).matches()) {
            addError(txtSupplierIdnew);
        }else{
            removeError(txtSupplierIdnew);
        }
    }
}
