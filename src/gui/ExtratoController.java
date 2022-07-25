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
import model.entities.Extrato;
import model.exceptions.ValidationException;
import model.services.AgendamentoService;
import model.services.ExtratoService;

public class ExtratoController implements Initializable {

	private Extrato entidade;
	
	private ExtratoService extratoService;
	
	private AgendamentoService agendamentoService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private Label labelPagamento;

	@FXML
	private Label labelErrorPagamento;

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtCliente;

	@FXML
	private TextField txtServico;

	@FXML
	private TextField txtValor;
	
	@FXML
	private TextField txtObservacao;

	@FXML
	private ComboBox<String> cbPagamento;

	@FXML
	private DatePicker dpData;

	@FXML
	private TextField txtHorario;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	private ObservableList<String> obsListPagamento;

	public void setEntidade(Extrato entidade) {
		this.entidade = entidade;
	}
	
	public void setServices(ExtratoService extratoService, AgendamentoService agendamentoService) {
		this.extratoService = extratoService;
		this.agendamentoService = agendamentoService;
	}

	@FXML
	public void onBtSalvar(ActionEvent event) {
		if (entidade == null) {
			throw new IllegalStateException("Entidade nula!");
		}

		try {

			entidade = getFormData();
			extratoService.insert(entidade);
			agendamentoService.deleteById(entidade.getId());
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
	}

	private void initializeNodes() {

		Utils.formatDatePicker(dpData, "dd/MM/yyyy");
		initializeComboBoxFormaPagamento();
	}

	private Extrato getFormData() {
		Extrato extrato = new Extrato();

		ValidationException exception = new ValidationException("Erro de validação!");
		
		extrato.setId(Utils.tryParseToInt(txtId.getText()));
		extrato.setCliente(txtCliente.getText());
		extrato.setServico(txtServico.getText());
		extrato.setValor(Utils.tryParseToDouble(txtValor.getText()));

		if (cbPagamento.getValue() == null) {
			exception.addError("Pagamento", "Campo não pode ficar vazio!");
		}
		extrato.setPagamento(cbPagamento.getValue());
		Instant instant = Instant.from(dpData.getValue().atStartOfDay(ZoneId.systemDefault()));
		extrato.setData(Date.from(instant));
		extrato.setHorario(LocalTime.parse(txtHorario.getText()));
		extrato.setObservacao(txtObservacao.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return extrato;
	}

	public void updateFormData() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade nula!");
		}

		txtId.setText(String.valueOf(entidade.getId()));
		txtCliente.setText(entidade.getCliente());
		txtServico.setText(entidade.getServico());
		txtValor.setText(String.valueOf(entidade.getValor()));
		dpData.setValue(LocalDate.ofInstant(entidade.getData().toInstant(), ZoneId.systemDefault()));
		txtHorario.setText(String.valueOf(entidade.getHorario()));
		txtObservacao.setText(entidade.getObservacao());

		if (cbPagamento == null) {
			cbPagamento.getSelectionModel().selectFirst();
		}

		cbPagamento.setValue(entidade.getPagamento());

	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("Pagamento")) {
			labelErrorPagamento.setText(errors.get("Pagamento"));
		
		} else {
			labelErrorPagamento.setText("");
		}

	}

	private void initializeComboBoxFormaPagamento() {
		Callback<ListView<String>, ListCell<String>> factory = lv -> new ListCell<String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.toString());
			}
		};
		cbPagamento.setCellFactory(factory);
		cbPagamento.setButtonCell(factory.call(null));
	}

	public void loadAssociateObjects() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade nula!");
		}

		List<String> list = entidade.getOpcoesPagamentoList();

		obsListPagamento = FXCollections.observableArrayList(list);
		cbPagamento.setItems(obsListPagamento);

	}
}