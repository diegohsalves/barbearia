package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Servico;
import model.services.ServicoService;

public class ServicoController implements Initializable, DataChangeListener {
	
	ServicoService servicoService;
	
	Servico entidade;
	
	@FXML
	private Button btNovo;

	@FXML
	private TableView<Servico> tableViewServico;

	@FXML
	private TableColumn<Servico, Integer> tableColumnId;

	@FXML
	private TableColumn<Servico, String> tableColumnDescricao;

	@FXML
	private TableColumn<Servico, Double> tableColumnValor;

	@FXML
	private TableColumn<Servico, Servico> tableColumnEdit;
	
	@FXML
	private TableColumn<Servico, Servico> tableColumnRemove;

	private ObservableList<Servico> obsList;
	
	public void setServicoService(ServicoService servicoService) {
		this.servicoService = servicoService;
	}
	
	public void setEntidade(Servico entidade) {
		this.entidade = entidade;
	}
	
	public void onBtNovo(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Servico obj = new Servico();
		createServicoForm(obj, "/gui/ServicoNovo.fxml", parentStage);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
		
	}
	
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnDescricao.setCellValueFactory(new PropertyValueFactory<>("Descricao"));
		tableColumnValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
		Utils.formatTableColumnDouble(tableColumnValor, 2);
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewServico.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (servicoService == null) {
			throw new IllegalStateException("Service nulo!"); // ?
		}
		List<Servico> list = servicoService.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewServico.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createServicoForm(Servico obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			ServicoNovoController controller = loader.getController();
			controller.setEntidade(obj);
			controller.setServicoService(new ServicoService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage cadastroStage = new Stage();
			cadastroStage.setTitle("Novo Serviço");
			cadastroStage.setScene(new Scene(pane));
			cadastroStage.setResizable(false);
			cadastroStage.initOwner(parentStage);
			cadastroStage.initModality(Modality.WINDOW_MODAL);
			cadastroStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro ao carregar a página!", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();

	}
	
	private void initEditButtons() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Servico, Servico>() {
			private final Button button = new Button("Edit");

			@Override
			protected void updateItem(Servico obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createServicoForm(obj, "/gui/ServicoNovo.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Servico, Servico>() {
			private final Button button = new Button("Remove");

			@Override
			protected void updateItem(Servico servico, boolean empty) {
				super.updateItem(servico, empty);
				if (servico == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(servico));
			}
		});
	}

	private void removeEntity(Servico servico) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Deseja mesmo deletar?");

		if (result.get() == ButtonType.OK) {
			if (servicoService == null) {
				throw new IllegalStateException("Service nulo!");

			}

			try {

				servicoService.remove(servico);
				updateTableView();

			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover objeto!", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}