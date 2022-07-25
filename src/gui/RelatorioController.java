package gui;

import java.net.URL;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Extrato;
import model.services.ExtratoService;

public class RelatorioController implements Initializable {
	
	private ExtratoService extratoService;
	
	@FXML
	private TextField txtBusca;
	
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
	private TableColumn<Extrato, String> tableColumnPagamento;
	
	@FXML
	private TableColumn<Extrato, Date> tableColumnData;
	
	@FXML
	private TableColumn<Extrato, LocalTime> tableColumnHorario;
	
	@FXML
	private Label labelTotal;
	
	private ObservableList<Extrato> obsList;
	
	public void setService(ExtratoService extratoService) {
		this.extratoService = extratoService;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
		
	}
	
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnCliente.setCellValueFactory(new PropertyValueFactory<>("Cliente"));
		tableColumnServico.setCellValueFactory(new PropertyValueFactory<>("Servico"));
		tableColumnValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
		Utils.formatTableColumnDouble(tableColumnValor, 2);
		tableColumnPagamento.setCellValueFactory(new PropertyValueFactory<>("Pagamento"));
		tableColumnData.setCellValueFactory(new PropertyValueFactory<>("Data"));
		Utils.formatTableColumnDate(tableColumnData, "dd/MM/yyyy");
		tableColumnHorario.setCellValueFactory(new PropertyValueFactory<>("Horario"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewExtrato.prefHeightProperty().bind(stage.heightProperty());
	}
	
	public void updateTableView() {
		if (extratoService == null) {
			throw new IllegalStateException("Service nulo!"); // ?
		}
		
		List<Extrato> list = extratoService.relatorioList();
		obsList = FXCollections.observableArrayList(list);
		tableViewExtrato.setItems(obsList);
		
		FilteredList<Extrato> filteredData = new FilteredList<>(obsList, x -> true);
		txtBusca.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(extrato -> {

				if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
					return true;
				}

				String buscaPalavra = newValue.toLowerCase();

				if (extrato.getCliente().toLowerCase().indexOf(buscaPalavra) > -1) {
					return true;

				} else if (extrato.getServico().toLowerCase().indexOf(buscaPalavra) > -1) { 
					return true;

				} else if (extrato.getServico().toLowerCase().indexOf(buscaPalavra) > -1) {
					return true;

				} else if (extrato.getPagamento().toLowerCase().indexOf(buscaPalavra) > -1) {
					return true;

				} else if (extrato.getValor().toString().indexOf(buscaPalavra) > -1) {
					return true;

				} else if (Utils.sdf.format(extrato.getData()).indexOf(buscaPalavra) > -1) {
					return true;

				} else {
					return false;

				}
			});
		});
		
		SortedList<Extrato> listaAtualizada = new SortedList<>(filteredData);
		
		listaAtualizada.comparatorProperty().bind(tableViewExtrato.comparatorProperty());
		tableViewExtrato.setItems(listaAtualizada);
		
		totalSum(obsList);

	}
	
	public void totalSum(ObservableList<Extrato> obsList) {
		
		Double soma = 0.0;
		
		for(Extrato extrato : obsList) {
			
			soma += extrato.getValor();	
		}
		
		String resultado = String.format("%.2f", soma);
		
		labelTotal.setText(resultado);
		
	}

}
