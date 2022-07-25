package model.dao;

import java.time.LocalDate;
import java.util.List;

import model.entities.Extrato;

public interface ExtratoDao {
	
	Extrato findById(Integer id);
	List<Extrato> findAll();
	void insert(Extrato obj);
	void update(Extrato obj);
	List<Extrato> relatorioList();
	List<Extrato> filteredList(LocalDate dataInicial, LocalDate dataFinal);

}
