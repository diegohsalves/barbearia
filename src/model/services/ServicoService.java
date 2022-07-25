package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.ServicoDao;
import model.entities.Servico;

public class ServicoService implements ServicoDao {

	ServicoDao dao = DaoFactory.createServicoDao();

	@Override
	public void insert(Servico obj) {
		dao.insert(obj);
	}

	@Override
	public void update(Servico obj) {
		dao.update(obj);
	}

	@Override
	public void deleteById(Integer id) {
		dao.deleteById(id);

	}

	@Override
	public Servico findById(Integer id) {
		return dao.findById(id);
	}

	@Override
	public List<Servico> findAll() {
		return dao.findAll();
	}

	public void saveOrUpdate(Servico obj) {
		if (obj.getId() == null) {
			dao.insert(obj);

		} else {
			dao.update(obj);
		}
	}

	public void remove(Servico servico) {
		dao.deleteById(servico.getId());
	}

}