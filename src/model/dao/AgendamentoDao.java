package model.dao;

import java.util.List;

import model.entities.Agendamento;

public interface AgendamentoDao {
	
	void insert(Agendamento obj);
	void update(Agendamento obj);
	void deleteById(Integer id);
	List<Agendamento> findAll();
	
}
