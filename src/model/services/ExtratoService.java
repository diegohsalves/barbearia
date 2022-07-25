package model.services;

import java.time.LocalDate;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.ExtratoDao;
import model.entities.Extrato;

public class ExtratoService {
	
	ExtratoDao dao = DaoFactory.createExtratoDao();
	
	public Extrato findById(Integer id) {
		return dao.findById(id);		
	}
	
	public List<Extrato> findAll(){
		return dao.findAll();
	}
	
	public void insert(Extrato extrato) {
		dao.insert(extrato);
	}
	
	public void saveOrUpdate(Extrato obj) {
		if (obj.getId() == null) {
			dao.insert(obj);

		} else {
			dao.update(obj);
		}
	}
	
	public List<Extrato> relatorioList() {
		return dao.relatorioList();
	}
	
	public List<Extrato> filteredList(LocalDate dataInicial, LocalDate dataFinal){
		return dao.filteredList(dataInicial, dataFinal);
	}

}
