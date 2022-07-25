package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Agendamento;
import model.entities.Extrato;
import model.services.AgendamentoService;
import model.services.ClienteService;
import model.services.ExtratoService;
import model.services.ServicoService;

public class PainelController implements Initializable, DataChangeListener {

	Helper helper = new Helper();

	private AgendamentoService agendamentoService;
	
	private ExtratoService extratoService;
	
	@FXML
	private MenuItem menuAreaPrincipal;

	@FXML
	private MenuItem menuAreaCliente;
	
	@FXML
	private MenuItem menuAgendamento;

	@FXML
	private MenuItem menuAreaServico;
	
	@FXML
	private MenuItem menuAreaRelatorio;

	@FXML
	private TableView<Extrato> tableViewExtrato;

	@FXML
	private TableColumn<Extrato, Integer> tableColumnId;

	@FXML
	private TableColumn<Extrato, String> tableColumnCliente;

	@FXML
	private TableColumn<Extrato, String> tableColumnServico;

	@FXML
	private TableColumn<Extrato, Double> tableColumnValor;

	@FXML
	private TableColumn<Extrato, Date> tableColumnData;

	@FXML
	private TableColumn<Extrato, LocalDateTime> tableColumnHorario;

	@FXML
	private TableColumn<Extrato, String> tableColumnObservacao;
	
	@FXML
	private TableColumn<Extrato, Extrato> tableColumnFinalizar;

	private ObservableList<Extrato> obsList;

	public void setServices(AgendamentoService agendamentoService, ExtratoService extratoService) {
		this.agendamentoService = agendamentoService;
		this.extratoService = extratoService;

	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnCliente.setCellValueFactory(new PropertyValueFactory<>("Cliente"));
		tableColumnServico.setCellValueFactory(new PropertyValueFactory<>("Servico"));
		tableColumnValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
		Utils.formatTableColumnDouble(tableColumnValor, 2);
		tableColumnData.setCellValueFactory(new PropertyValueFactory<>("Data"));
		Utils.formatTableColumnDate(tableColumnData, "dd/MM/yyyy");
		tableColumnHorario.setCellValueFactory(new PropertyValueFactory<>("Horario"));
		tableColumnObservacao.setCellValueFactory(new PropertyValueFactory<>("Observacao"));
		initFinalizarButtons();

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewExtrato.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (agendamentoService == null) {
			throw new IllegalStateException("Service nulo!");
		}
		List<Agendamento> list = agendamentoService.findAll();
		obsList = FXCollections.observableArrayList(Utils.converter(list));
		tableViewExtrato.setItems(obsList);
	}

	public void onMenuItemMenuAreaCliente() {
		helper.loadview("/gui/Cliente.fxml", (ClienteController controller) -> {
			controller.setClienteService(new ClienteService());
			controller.updateTableView();
		});
	}

	public void onMenuItemMenuAreaServico() {
		helper.loadview("/gui/Servico.fxml", (ServicoController controller) -> {
			controller.setServicoService(new ServicoService());
			controller.updateTableView();
		});

	}
	
	public void onMenuItemMenuAreaPrincipal() {
		helper.loadPainelView("/gui/Painel.fxml", (PainelController controller) -> {
			controller.setServices(new AgendamentoService(), new ExtratoService());
			controller.updateTableView();
		});
	}
	
	public void onMenuAgendamento() {
		helper.loadview("/gui/Agendamento.fxml", (AgendamentoController controller) -> {
			controller.setAgendamentoService(new AgendamentoService());
			controller.updateTableView();
		});
	}
	
	public void onMenuItemMenuAreaRelatorio() {
		helper.loadview("/gui/Relatorio.fxml", (RelatorioController controller) -> {
			controller.setService(new ExtratoService());
			controller.updateTableView();
		});

	}
	
	private void initFinalizarButtons() {
		tableColumnFinalizar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnFinalizar.setCellFactory(param -> new TableCell<Extrato, Extrato>() {
			private final Button button = new Button("Finalizar");

			@Override
			protected void updateItem(Extrato extrato, boolean empty) {
				super.updateItem(extrato, empty);
				if (extrato == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> finalizarEntity(extrato, event));
			}
		});
	}

	private void finalizarEntity(Extrato extrato, ActionEvent event) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Deseja mesmo finalizar?");

		if (result.get() == ButtonType.OK) {
			if (extratoService == null || agendamentoService == null) {
				throw new IllegalStateException("Service nulo!");

			}

			try {
				
				serviceDone(extrato, "/gui/Extrato.fxml", Utils.currentStage(event));
				updateTableView();

			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao finalizar serviço!", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
	private synchronized void serviceDone(Extrato obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			ExtratoController controller = loader.getController();
			controller.setEntidade(obj);
			controller.setServices(new ExtratoService(), new AgendamentoService());
			controller.loadAssociateObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage cadastroStage = new Stage();
			cadastroStage.setTitle("Finalizar");
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
}