package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.Date;
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
import model.entities.Agendamento;
import model.entities.Cliente;
import model.entities.Servico;
import model.services.AgendamentoService;
import model.services.ClienteService;
import model.services.ServicoService;

public class AgendamentoController implements Initializable, DataChangeListener {

	AgendamentoService agendamentoService;
	Helper helper = new Helper();

	@FXML
	private Button btNovo;
	
	@FXML
	private TextField txtBusca;

	@FXML
	private TableView<Agendamento> tableViewAgendamento;

	@FXML
	private TableColumn<Agendamento, Integer> tableColumnId;

	@FXML
	private TableColumn<Agendamento, Cliente> tableColumnCliente;

	@FXML
	private TableColumn<Agendamento, Servico> tableColumnServico;

	@FXML
	private TableColumn<Agendamento, Date> tableColumnData;

	@FXML
	private TableColumn<Agendamento, LocalTime> tableColumnHorario;

	@FXML
	private TableColumn<Agendamento, String> tableColumnObservacao;

	@FXML
	private TableColumn<Agendamento, Agendamento> tableColumnEdit;

	@FXML
	private TableColumn<Agendamento, Agendamento> tableColumnRemove;

	private ObservableList<Agendamento> obsList;

	public void setAgendamentoService(AgendamentoService agendamentoService) {
		this.agendamentoService = agendamentoService;
	}

	public void onBtNovo(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Agendamento agendamento = new Agendamento();
		createAgendamentoNovo(agendamento, "/gui/AgendamentoNovo.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnCliente.setCellValueFactory(new PropertyValueFactory<>("Cliente"));
		tableColumnServico.setCellValueFactory(new PropertyValueFactory<>("Servico"));
		tableColumnData.setCellValueFactory(new PropertyValueFactory<>("Data"));
		Utils.formatTableColumnDate(tableColumnData, "dd/MM/yyyy");
		tableColumnHorario.setCellValueFactory(new PropertyValueFactory<>("Horario"));
		tableColumnObservacao.setCellValueFactory(new PropertyValueFactory<>("Observacao"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewAgendamento.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (agendamentoService == null) {
			throw new IllegalStateException("Service nulo!"); // ?
		}
		List<Agendamento> list = agendamentoService.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewAgendamento.setItems(obsList);
		
		FilteredList<Agendamento> filtroDados = new FilteredList<>(obsList, x -> true);
		txtBusca.textProperty().addListener((observable, oldValue, newValue) -> {
			filtroDados.setPredicate(agendamento -> {

				if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
					return true;
				}

				String buscaPalavra = newValue.toLowerCase();

				if (agendamento.getCliente().getNome().toLowerCase().indexOf(buscaPalavra) > -1) {
					return true;

				} else if (Utils.sdf.format(agendamento.getData()).indexOf(buscaPalavra) > -1) {
					return true;

				} else if (agendamento.getHorario().toString().indexOf(buscaPalavra) > -1) {
					return true;

				} else if (agendamento.getServico().getDescricao().toLowerCase().indexOf(buscaPalavra) > -1) {
					return true;

				} else {
					return false;

				}
			});
		});
		
		SortedList<Agendamento> listaAtualizada = new SortedList<>(filtroDados);
		
		listaAtualizada.comparatorProperty().bind(tableViewAgendamento.comparatorProperty());
		tableViewAgendamento.setItems(listaAtualizada);
		
		
		initEditButtons();
		initRemoveButtons();
	}

	private void createAgendamentoNovo(Agendamento agendamento,
			String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			AgendamentoNovoController controller = loader.getController();
			controller.setEntidade(agendamento);
			controller.setServices(new ClienteService(), new AgendamentoService(), new ServicoService());
			controller.loadAssociateObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage cadastroStage = new Stage();
			cadastroStage.setTitle("Novo Agendamento");
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
		tableColumnEdit.setCellFactory(param -> new TableCell<Agendamento, Agendamento>() {
			private final Button button = new Button("Edit");

			@Override
			protected void updateItem(Agendamento agendamento, boolean empty) {
				super.updateItem(agendamento, empty);
				if (agendamento == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createAgendamentoNovo(agendamento, "/gui/AgendamentoNovo.fxml",
						Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Agendamento, Agendamento>() {
			private final Button button = new Button("Remove");

			@Override
			protected void updateItem(Agendamento agendamento, boolean empty) {
				super.updateItem(agendamento, empty);
				if (agendamento == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(agendamento));
			}
		});
	}

	private void removeEntity(Agendamento agendamento) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Deseja mesmo deletar?");

		if (result.get() == ButtonType.OK) {
			if (agendamentoService == null) {
				throw new IllegalStateException("Service nulo!");

			}

			try {

				agendamentoService.remove(agendamento);
				updateTableView();

			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover objeto!", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}