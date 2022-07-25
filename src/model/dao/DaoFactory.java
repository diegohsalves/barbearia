package model.dao;

import db.DB;
import model.dao.impl.AgendamentoDaoJDBC;
import model.dao.impl.ClienteDaoJDBC;
import model.dao.impl.ExtratoDaoJDBC;
import model.dao.impl.ServicoDaoJDBC;

public class DaoFactory {
	
	public static ClienteDao createClienteDao() {
		return new ClienteDaoJDBC(DB.getConnection());
	}
	
	public static AgendamentoDao createAgendamentoDao() {
		return new AgendamentoDaoJDBC(DB.getConnection());
	}
	
	public static ServicoDao createServicoDao() {
		return new ServicoDaoJDBC(DB.getConnection());
	}
	
	public static ExtratoDao createExtratoDao() {
		return new ExtratoDaoJDBC(DB.getConnection());
	}

}
