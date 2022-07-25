package model.services;

import java.util.List;

import model.dao.AgendamentoDao;
import model.dao.DaoFactory;
import model.entities.Agendamento;

public class AgendamentoService {
	
	private AgendamentoDao dao = DaoFactory.createAgendamentoDao();
	
	public void insert(Agendamento obj){ 
		dao.insert(obj);
	}
	
	public void update(Agendamento obj) {
		dao.update(obj);
	}
	
	public void deleteById(Integer id) {
		dao.deleteById(id);
	}
	
	public List<Agendamento> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate(Agendamento obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
	
		} else {
			dao.update(obj);
		}
	}

	public void remove(Agendamento agendamento) {
			dao.deleteById(agendamento.getId());
		}
		
	}