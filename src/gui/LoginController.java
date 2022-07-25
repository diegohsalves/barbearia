package gui;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import db.DB;
import gui.util.Helper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.services.AgendamentoService;
import model.services.ExtratoService;

public class LoginController implements Initializable{
	
	Helper helper = new Helper();
	
	@FXML
	private Label label_usuario;
	
	@FXML
	private TextField txtUsuario;
	
	@FXML
	private Label label_senha;
	
	@FXML
	private PasswordField txtSenha;
	
	@FXML
	private Button btEntrar;
	
	@FXML
	private Label label_mensagem;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
	}
	
	public void onBtEntrarAction() {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String usuario = txtUsuario.getText();
		String senha = txtSenha.getText();
		
		try {
			conn = DB.getConnection();
			ps = conn.prepareStatement("SELECT senha FROM login WHERE usuario = ?");
			ps.setString(1, usuario);
			rs = ps.executeQuery();
			
			if(!rs.isBeforeFirst()) {
				label_mensagem.setText("Usuário não encontrado!");		
				
			} else {
				while(rs.next()) {
					String senhaBanco = rs.getString("senha");
					
					if(senhaBanco.equals(senha)) {
						helper.loadPainelView("/gui/Painel.fxml", (PainelController controller) -> {
							controller.setServices(new AgendamentoService(), new ExtratoService());
							controller.updateTableView();
						});
					
					} else {
						label_mensagem.setText("Senha inválida!");
						
					}
				}
			}
		} catch(SQLException e) {
			e.getMessage();
		}
	}
}