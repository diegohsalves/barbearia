package gui.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.entities.Agendamento;
import model.entities.Extrato;

public class Utils {

	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}

	public static Integer tryParseToInt(String str) {

		try {
			return Integer.parseInt(str);

		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Double tryParseToDouble(String str) {

		try {
			return Double.parseDouble(str);

		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static void formatDatePicker(DatePicker datePicker, String format) {
		datePicker.setConverter(new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);
			{
				datePicker.setPromptText(format.toLowerCase());
			}

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});
	}

	public static List<LocalTime> createHorario() {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

		List<LocalTime> list = Arrays.asList(LocalTime.parse("09:00", dtf), LocalTime.parse("10:00", dtf),
				LocalTime.parse("11:00", dtf), LocalTime.parse("14:00", dtf), LocalTime.parse("15:00", dtf),
				LocalTime.parse("16:00", dtf), LocalTime.parse("17:00", dtf), LocalTime.parse("18:00", dtf));

		return list;
	}

	public static <T> void formatTableColumnDate(TableColumn<T,Date> tableColumn, String format) {
		
		tableColumn.setCellFactory(column -> {
			TableCell<T, Date> cell = new TableCell<T, Date>() {
				private SimpleDateFormat sdf = new SimpleDateFormat(format);

				@Override
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						setText(sdf.format(item));
					}
				}
			};
			return cell;
		});
	}

	public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, int decimalPlaces) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Double> cell = new TableCell<T, Double>() {
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						setText(String.format("%." + decimalPlaces + "f", item));
					}
				}
			};
			return cell;
		});
	}
	
	public static List<Extrato> converter(List<Agendamento> list){
		
		List<Extrato> extrato = new ArrayList<>();
		
		for(Agendamento a : list) {
			Extrato e = new Extrato();
			e.setId(a.getId());
			e.setCliente(a.getCliente().getNome());
			e.setServico(a.getServico().getDescricao());
			e.setValor(a.getServico().getValor());
			e.setData(a.getData());
			e.setHorario(a.getHorario());
			e.setObservacao(a.getObservacao());
			
			extrato.add(e);
		}
		
		return extrato;
	}
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
}
