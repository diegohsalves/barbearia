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
import gui.util.Helper;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Cliente;
import model.services.ClienteService;

public class ClienteController implements Initializable, DataChangeListener {

	ClienteService clienteService;
	Helper helper = new Helper();

	@FXML
	private Button btNovo;

	@FXML
	private TextField txtBusca;

	@FXML
	private TableView<Cliente> tableViewCliente;

	@FXML
	private TableColumn<Cliente, Integer> tableColumnId;

	@FXML
	private TableColumn<Cliente, String> tableColumnNome;

	@FXML
	private TableColumn<Cliente, String> tableColumnCpf;

	@FXML
	private TableColumn<Cliente, String> tableColumnTelefone;

	@FXML
	private TableColumn<Cliente, String> tableColumnEndereco;

	@FXML
	private TableColumn<Cliente, String> tableColumnEmail;

	@FXML
	private TableColumn<Cliente, Cliente> tableColumnEdit;

	@FXML
	private TableColumn<Cliente, Cliente> tableColumnRemove;

	private ObservableList<Cliente> obsList;

	public void setClienteService(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	public void onBtNovo(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Cliente obj = new Cliente();
		createClienteNovo(obj, "/gui/ClienteNovo.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("Nome"));
		tableColumnCpf.setCellValueFactory(new PropertyValueFactory<>("Cpf"));
		tableColumnTelefone.setCellValueFactory(new PropertyValueFactory<>("Telefone"));
		tableColumnEndereco.setCellValueFactory(new PropertyValueFactory<>("Endereco"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewCliente.prefHeightProperty().bind(stage.heightProperty());

	}

	public void updateTableView() {
		if (clienteService == null) {
			throw new IllegalStateException("Service nulo!"); // ?
		}
		List<Cliente> list = clienteService.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewCliente.setItems(obsList);
		
		FilteredList<Cliente> filtroDados = new FilteredList<>(obsList, x -> true);
		txtBusca.textProperty().addListener((observable, oldValue, newValue) -> {
			filtroDados.setPredicate(cliente -> {

				if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
					return true;
				}

				String buscaPalavra = newValue.toLowerCase();

				if (cliente.getNome().toLowerCase().indexOf(buscaPalavra) > -1) {
					return true;

				} else if (cliente.getCpf().indexOf(buscaPalavra) > -1) {
					return true;

				} else if (cliente.getTelefone().indexOf(buscaPalavra) > -1) {
					return true;

				} else if (cliente.getEmail().toLowerCase().indexOf(buscaPalavra) > -1) {
					return true;

				} else if (cliente.getEndereco().toLowerCase().indexOf(buscaPalavra) > -1) {
					return true;

				} else if (cliente.getId().toString().indexOf(buscaPalavra) > -1) {
					return true;

				} else {
					return false;

				}
			});
		});
		
		SortedList<Cliente> listaAtualizada = new SortedList<>(filtroDados);
		
		listaAtualizada.comparatorProperty().bind(tableViewCliente.comparatorProperty());
		tableViewCliente.setItems(listaAtualizada);
		
		initEditButtons();
		initRemoveButtons();
	}

	private void createClienteNovo(Cliente obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			ClienteNovoController controller = loader.getController();
			controller.setEntidade(obj);
			controller.setClienteService(new ClienteService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage cadastroStage = new Stage();
			cadastroStage.setTitle("Novo Cliente");
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
		tableColumnEdit.setCellFactory(param -> new TableCell<Cliente, Cliente>() {
			private final Button button = new Button("Edit");

			@Override
			protected void updateItem(Cliente obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createClienteNovo(obj, "/gui/ClienteNovo.fxml",
						Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Cliente, Cliente>() {
			private final Button button = new Button("Remove");

			@Override
			protected void updateItem(Cliente obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Cliente obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Deseja mesmo deletar?");

		if (result.get() == ButtonType.OK) {
			if (clienteService == null) {
				throw new IllegalStateException("Service nulo!");

			}

			try {

				clienteService.remove(obj);
				updateTableView();

			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover objeto!", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}