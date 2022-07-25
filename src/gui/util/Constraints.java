package gui.util;

import javafx.scene.control.TextField;

public class Constraints {
	public static void setTextFieldInteger(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue == null || !newValue.matches("\\d*")) {
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldMaxLength(TextField txt, int max) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.length() > max) {
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldDouble(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {

			if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
				txt.setText(oldValue);
			}
		});
	}
	
	public static void setTextFieldCpf(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
				
				if(txt != null && newValue != null && txt.getText().length()==3){
					txt.setText(txt.getText()+".");
					txt.positionCaret(txt.getText().length());
	            }
	            if(txt != null && newValue != null && txt.getText().length()==7){
	            	txt.setText(txt.getText()+".");
	            	txt.positionCaret(txt.getText().length());
	            }
	            if(txt != null && newValue != null && txt.getText().length()==11){
	            	txt.setText(txt.getText()+"-");
	            	txt.positionCaret(txt.getText().length());
	            }
	            
	            if (newValue == null || newValue.matches("((\\d{3}).(\\d{3}).(\\d{3})-(\\d{3}))")) {
					txt.setText(oldValue);
	            }
		
		});
	}
	
	public static void setTextFieldTelefone(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			
			if(txt != null && newValue != null && txt.getText().length()==1){
				txt.setText("(" + txt.getText());
				txt.positionCaret(txt.getText().length());
            }
            if(txt != null && newValue != null && txt.getText().length()==3){
            	txt.setText(txt.getText()+")");
            	txt.positionCaret(txt.getText().length());
            }
            if(txt != null && newValue != null && txt.getText().length()==9){
            	txt.setText(txt.getText()+"-");
            	txt.positionCaret(txt.getText().length());
            }
			
			if (newValue == null || newValue.matches("([\\(](\\d{2})[\\)](\\d{5})-(\\d{5}))")) { 
				txt.setText(oldValue);
			}
		});
	}
	
	public static void setTextFieldCliente(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				txt.setText(oldValue);
			}
		});
	}
	
	public static void setTextFieldServico(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				txt.setText(oldValue);
			}
		});
	}
	
	public static void setTextFieldHorario(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				txt.setText(oldValue);
			}
		});
	}
}