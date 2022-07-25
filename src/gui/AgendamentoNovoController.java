package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Agendamento;
import model.entities.Cliente;
import model.entities.Servico;
import model.exceptions.ValidationException;
import model.services.AgendamentoService;
import model.services.ClienteService;
import model.services.ServicoService;

public class AgendamentoNovoController implements Initializable {

	private Agendamento entidade;

	private ClienteService clienteService;
	private AgendamentoService agendamentoService;
	private ServicoService servicoService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private Label labelErrorCliente;

	@FXML
	private Label labelErrorId;

	@FXML
	private Label labelErrorServico;

	@FXML
	private Label labelErrorData;

	@FXML
	private Label labelErrorHorario;

	@FXML
	private Label labelErrorObservacao;

	@FXML
	private TextField txtId;

	@FXML
	private ComboBox<Cliente> cbCliente;

	@FXML
	private ComboBox<Servico> cbServico;

	@FXML
	private ComboBox<LocalTime> cbHorario;

	@FXML
	private DatePicker dpData;

	@FXML
	private TextField txtObservacao;

	private ObservableList<Cliente> obsListCliente;

	private ObservableList<Servico> obsListServico;

	private ObservableList<LocalTime> obsListHorario;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	public void setServices(ClienteService clienteService, AgendamentoService agendamentoService,
			ServicoService servicoService) {
		this.clienteService = clienteService;
		this.agendamentoService = agendamentoService;
		this.servicoService = servicoService;
	}

	public void setEntidade(Agendamento agendamento) {
		this.entidade = agendamento;
	}

	@FXML
	public void onBtSalvar(ActionEvent event) {
		if (entidade == null) {
			throw new IllegalStateException("Entidade nula!");
		}
		if (agendamentoService == null) {
			throw new IllegalStateException("Service nulo!");
		}
		try {

			entidade = getFormData();
			agendamentoService.saveOrUpdate(entidade);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();

		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}

	}

	@FXML
	public void onBtCancelar(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	private Agendamento getFormData(){
		Agendamento agendamento = new Agendamento();

		ValidationException exception = new ValidationException("Erro de validação!");

		agendamento.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(cbCliente.getValue() == null) {
			exception.addError("Cliente", "Campo não pode ficar vazio!");
		}
		
		agendamento.setCliente(cbCliente.getValue());
		
		if(cbServico.getValue() == null) {
			exception.addError("Servico", "Campo não pode ficar vazio!");
		}
		
		agendamento.setServico(cbServico.getValue());

		if (dpData.getValue() == null) {
			exception.addError("Data", "Campo não pode ficar vazio!");
		
		} else {
			Instant instant = Instant.from(dpData.getValue().atStartOfDay(ZoneId.systemDefault()));
			agendamento.setData(Date.from(instant));
		}

		if(cbHorario.getValue() == null) {
			exception.addError("Horario", "Campo não pode ficar vazio!");
		}
		
		agendamento.setHorario(cbHorario.getValue());
		agendamento.setObservacao(txtObservacao.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return agendamento;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {

		Constraints.setTextFieldInteger(txtId);
		Utils.formatDatePicker(dpData, "dd/MM/yyyy");
		initializeComboBoxCliente();
		initializeComboBoxServico();
		initializeComboBoxHorario();
	}

	public void updateFormData() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade nula!");
		}

		txtId.setText(String.valueOf(entidade.getId()));
		txtObservacao.setText(entidade.getObservacao());

		if (entidade.getData() != null) {
			dpData.setValue(LocalDate.ofInstant(entidade.getData().toInstant(), ZoneId.systemDefault()));
		}

		if (cbCliente == null) {
			cbCliente.getSelectionModel().selectFirst();
		}

		if (cbServico == null) {
			cbServico.getSelectionModel().selectFirst();
		}

		if (cbHorario == null) {
			cbHorario.getSelectionModel().selectFirst();
		}

		cbCliente.setValue(entidade.getCliente());
		cbServico.setValue(entidade.getServico());
		cbHorario.setValue(entidade.getHorario());
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("Data")) {
			labelErrorData.setText(errors.get("Data"));
		
		} else {
			labelErrorData.setText("");
		
		}
		
		if (fields.contains("Cliente")) {
			labelErrorCliente.setText(errors.get("Cliente"));
		
		} else {
			labelErrorCliente.setText("");
		
		}
		
		if (fields.contains("Servico")) {
			labelErrorServico.setText(errors.get("Servico"));
		
		} else {
			labelErrorServico.setText("");
		
		}
		
		if (fields.contains("Horario")) {
			labelErrorHorario.setText(errors.get("Horario"));
		
		} else {
			labelErrorHorario.setText("");
		
		}

	}

	private void initializeComboBoxCliente() {
		Callback<ListView<Cliente>, ListCell<Cliente>> factory = lv -> new ListCell<Cliente>() {
			@Override
			protected void updateItem(Cliente item, boolean empty) {
				super.updateItem(item, empty);
					setText(empty ? "" : item.getNome());
			}
		};
		cbCliente.setCellFactory(factory);
		cbCliente.setButtonCell(factory.call(null));
	}

	private void initializeComboBoxServico() {
		Callback<ListView<Servico>, ListCell<Servico>> factory = lv -> new ListCell<Servico>() {
			@Override
			protected void updateItem(Servico item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getDescricao());
			}
		};
		cbServico.setCellFactory(factory);
		cbServico.setButtonCell(factory.call(null));
	}

	private void initializeComboBoxHorario() {
		Callback<ListView<LocalTime>, ListCell<LocalTime>> factory = lv -> new ListCell<LocalTime>() {
			@Override

			protected void updateItem(LocalTime item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.toString());
			}
		};
		cbHorario.setCellFactory(factory);
		cbHorario.setButtonCell(factory.call(null));
	}

	public void loadAssociateObjects() {
		if (clienteService == null || servicoService == null) {
			throw new IllegalStateException("Service nulo!");
		}

		List<Cliente> clienteList = clienteService.findAll();
		List<Servico> servicoList = servicoService.findAll();
		List<LocalTime> horarioList = Utils.createHorario();

		obsListCliente = FXCollections.observableArrayList(clienteList);
		cbCliente.setItems(obsListCliente);

		obsListServico = FXCollections.observableArrayList(servicoList);
		cbServico.setItems(obsListServico);

		obsListHorario = FXCollections.observableArrayList(horarioList);
		cbHorario.setItems(obsListHorario);
	}
}
