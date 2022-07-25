package model.services;

import java.util.List;

import model.dao.ClienteDao;
import model.dao.DaoFactory;
import model.entities.Cliente;

public class ClienteService {

	private ClienteDao dao = DaoFactory.createClienteDao();

	public List<Cliente> findAll() {
		return dao.findAll();
	}

	public Cliente findByCpf(String cpf) {
		return dao.findByCpf(cpf);
	}

	public void insert(Cliente obj) {
		dao.insert(obj);
	}

	public void update(Cliente obj) {
		dao.update(obj);
	}

	public void deleteByCpf(String cpf) {
		dao.deleteByCpf(cpf);
	}

	public void saveOrUpdate(Cliente obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
	
		} else {
			dao.update(obj);
		}
	}
	
	public void remove(Cliente obj) {
		dao.deleteByCpf(obj.getCpf());
	}
}
