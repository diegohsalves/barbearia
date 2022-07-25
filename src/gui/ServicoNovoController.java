package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Servico;
import model.exceptions.ValidationException;
import model.services.ServicoService;

public class ServicoNovoController implements Initializable {

	Servico entidade;

	ServicoService servicoService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private Label labelErrorId;

	@FXML
	private Label labelErrorDescricao;

	@FXML
	private Label labelErrorValor;

	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtDescricao;

	@FXML
	private TextField txtValor;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	public void setServicoService(ServicoService servicoService) {
		this.servicoService = servicoService;
	}

	public void setEntidade(Servico servico) {
		this.entidade = servico;
	}

	@FXML
	public void onBtSalvar(ActionEvent event) {
		if (entidade == null) {
			throw new IllegalStateException("Entidade nula!");
		}
		if (servicoService == null) {
			throw new IllegalStateException("Service nulo!");
		}
		try {

			entidade = getFormData();
			servicoService.saveOrUpdate(entidade);
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

	private Servico getFormData() {
		Servico servico = new Servico();

		ValidationException exception = new ValidationException("Erro de validação!");
		
		servico.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtDescricao.getText() == null || txtDescricao.getText().trim().equals("")) {
			exception.addError("Descricao", "O campo não pode ser vazio!");
		}
		
		servico.setDescricao(txtDescricao.getText());

		if (txtValor.getText() == null || txtValor.getText().equals("")) {
			exception.addError("Valor", "O campo não pode ser vazio!");
		}
		
		if (txtValor.getText() != null && !txtValor.getText().matches("((\\d*)(\\.)(\\d{2}))")) {
			exception.addError("Valor", "Padrão inválido!");
		}
		
		servico.setValor(Utils.tryParseToDouble(txtValor.getText()));
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return servico;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	public void updateFormData() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade nula!");
		}
		
		txtId.setText(String.valueOf(entidade.getId()));
		txtDescricao.setText(entidade.getDescricao());
		txtValor.setText(String.valueOf(entidade.getValor()));
	}
	
	private void initializeNodes() {

		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldDouble(txtValor);

	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("Descricao")) {
			labelErrorDescricao.setText(errors.get("Descricao"));
		
		} else {
			labelErrorDescricao.setText("");
		
		}

		if (fields.contains("Valor")) {
			labelErrorValor.setText(errors.get("Valor"));
		
		} else {
			labelErrorValor.setText("");
		
		}

	}
}