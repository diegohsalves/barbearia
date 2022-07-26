package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.ClienteDao;
import model.entities.Cliente;

public class ClienteDaoJDBC implements ClienteDao {

	private Connection conn;

	public ClienteDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public Cliente findByCpf(String cpf) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM cliente WHERE cpf = ?");
			st.setString(1, cpf);
			rs = st.executeQuery();
			if (rs.next()) {
				Cliente obj = new Cliente();
				obj.setId(rs.getInt("Id"));
				obj.setNome(rs.getString("Nome"));
				obj.setCpf(rs.getString("Cpf"));
				obj.setTelefone(rs.getString("Telefone"));
				obj.setEndereco(rs.getString("Endereco"));
				obj.setEmail(rs.getString("Email"));
				return obj;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Cliente> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM cliente ORDER BY Nome");
			rs = st.executeQuery();

			List<Cliente> list = new ArrayList<>();

			while (rs.next()) {
				Cliente obj = new Cliente();
				obj.setId(rs.getInt("Id"));
				obj.setNome(rs.getString("Nome"));
				obj.setCpf(rs.getString("Cpf"));
				obj.setTelefone(rs.getString("Telefone"));
				obj.setEndereco(rs.getString("Endereco"));
				obj.setEmail(rs.getString("Email"));
				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public void insert(Cliente obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO cliente " + "(id, nome, cpf, telefone, endereco, email)" + "VALUES "
					+ "(DEFAULT,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getNome());
			st.setString(2, obj.getCpf());
			st.setString(3, obj.getTelefone());
			st.setString(4, obj.getEndereco());
			st.setString(5, obj.getEmail());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);

			} else {
				throw new DbException("Erro inesperado! Nenhuma linha foi afetada!");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Cliente obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE cliente " + "SET Nome = ?, Cpf = ?, Telefone = ?, Endereco = ?, Email = ? WHERE Id = ?");

			st.setString(1, obj.getNome());
			st.setString(2, obj.getCpf());
			st.setString(3, obj.getTelefone());
			st.setString(4, obj.getEndereco());
			st.setString(5, obj.getEmail());
			st.setInt(6, obj.getId());

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteByCpf(String cpf) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM cliente WHERE cpf = ?");

			st.setString(1, cpf);

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}
}