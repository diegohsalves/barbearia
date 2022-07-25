package model.dao;

import java.util.List;

import model.entities.Servico;

public interface ServicoDao {
	
	void insert(Servico obj);
	void update(Servico obj);
	void deleteById(Integer id);
	Servico findById(Integer id);
	List<Servico> findAll();

}
