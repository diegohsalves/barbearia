package model.dao;

import java.util.List;

import model.entities.Cliente;

public interface ClienteDao {
	
	void insert(Cliente obj);
	void update(Cliente obj);
	void deleteByCpf(String cpf);
    Cliente findByCpf(String cpf);
	List<Cliente> findAll();

}
