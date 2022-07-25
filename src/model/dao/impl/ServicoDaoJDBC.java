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
import model.dao.ServicoDao;
import model.entities.Servico;

public class ServicoDaoJDBC implements ServicoDao {

	private Connection conn;

	public ServicoDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Servico obj) {

		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO servico " + "(Descricao, Valor)" + "VALUES " + "(?,?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getDescricao());
			st.setDouble(2, obj.getValor());

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
	public void update(Servico obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE servico " + "SET Descricao = ?, Valor = ?" + "WHERE Id = ?");

			st.setString(1, obj.getDescricao());
			st.setDouble(2, obj.getValor());
			st.setInt(3, obj.getId());

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM servico WHERE Id = ?");

			st.setInt(1, id);

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Servico findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM servico WHERE Id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();

			if (rs.next()) {
				Servico obj = new Servico();
				obj.setDescricao(rs.getString("Descricao"));
				obj.setValor(rs.getDouble("Valor"));

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
	public List<Servico> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM servico ORDER BY Id");
			rs = st.executeQuery();

			List<Servico> list = new ArrayList<>();

			while (rs.next()) {

				Servico obj = new Servico();
				obj.setId(rs.getInt("Id"));
				obj.setDescricao(rs.getString("Descricao"));
				obj.setValor(rs.getDouble("Valor"));
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

}
