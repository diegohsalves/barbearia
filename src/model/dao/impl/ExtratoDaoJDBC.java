package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.ExtratoDao;
import model.entities.Extrato;

public class ExtratoDaoJDBC implements ExtratoDao {

	private Connection conn;

	public ExtratoDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public Extrato findById(Integer id) {

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT ag.Id," + "	c.Nome AS Cliente," + "    serv.Descricao AS Servico,"
					+ "    serv.Valor," + "    ag.Data," + "    ag.Horario," + "    ag.Observacao"
					+ "    FROM agendamento AS ag" + "    JOIN cliente AS c ON c.Id = ag.Cliente_Id"
					+ "    JOIN servico as serv ON ag.Servico_Id = serv.Id WHERE ag.Id = ?");

			st.setInt(1, id);

			rs = st.executeQuery();

			if (rs.next()) {

				Extrato obj = new Extrato();
				obj.setId(rs.getInt("Id"));
				obj.setCliente(rs.getString("Cliente"));
				obj.setServico(rs.getString("Servico"));
				obj.setValor(rs.getDouble("Valor"));
				obj.setData(new java.util.Date(rs.getTimestamp("Data").getTime()));
				obj.setHorario(rs.getTime("Horario").toLocalTime());
				obj.setObservacao(rs.getString("Observacao"));

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
	public void update(Extrato obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE extrato "
					+ "SET Id = ?, Cliente = ?, Servico = ?, Valor = ?, Pagamento = ?, Data = ?, Horario = ?, Observacao = ? "
					+ "WHERE id = ?");

			st.setInt(1, obj.getId());
			st.setString(2, obj.getCliente());
			st.setString(3, obj.getServico());
			st.setDouble(4, obj.getValor());
			st.setString(5, obj.getPagamento());
			st.setDate(6, new java.sql.Date(obj.getData().getTime()));
			st.setTime(7, Time.valueOf(obj.getHorario()));
			st.setString(8, obj.getObservacao());
			st.setInt(9, obj.getId());

			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Extrato> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT ag.Id," + "	c.Nome AS Cliente," + "    serv.Descricao AS Servico,"
					+ "    serv.Valor," + "    ag.Data," + "    ag.Horario," + "    ag.Observacao"
					+ "    FROM agendamento AS ag" + "    JOIN cliente AS c ON c.Id = ag.Cliente_Id"
					+ "    JOIN servico as serv ON ag.Servico_Id = serv.Id");

			rs = st.executeQuery();

			List<Extrato> list = new ArrayList<>();

			while (rs.next()) {

				Extrato obj = new Extrato();
				obj.setId(rs.getInt("Id"));
				obj.setCliente(rs.getString("Cliente"));
				obj.setServico(rs.getString("Servico"));
				obj.setValor(rs.getDouble("Valor"));
				obj.setData(new java.util.Date(rs.getTimestamp("Data").getTime()));
				obj.setHorario(rs.getTime("Horario").toLocalTime());
				obj.setObservacao(rs.getString("Observacao"));

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
	public void insert(Extrato obj) {

		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO extrato "
					+ "(Id, Cliente, Servico, Valor, Pagamento, Data, Horario, Observacao) " + "VALUES " + "(?,?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setInt(1, obj.getId());
			st.setString(2, obj.getCliente());
			st.setString(3, obj.getServico());
			st.setDouble(4, obj.getValor());
			st.setString(5, obj.getPagamento());
			st.setDate(6, new java.sql.Date(obj.getData().getTime()));
			st.setTime(7, Time.valueOf(obj.getHorario()));
			st.setString(8, obj.getObservacao());

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
	public List<Extrato> relatorioList() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM extrato"
					+ "   ORDER BY extrato.Data, extrato.Horario");
					
			rs = st.executeQuery();

			List<Extrato> list = new ArrayList<>();

			while (rs.next()) {

				Extrato obj = new Extrato();
				obj.setId(rs.getInt("Id"));
				obj.setCliente(rs.getString("Cliente"));
				obj.setServico(rs.getString("Servico"));
				obj.setPagamento(rs.getString("Pagamento"));
				obj.setValor(rs.getDouble("Valor"));
				obj.setData(new java.util.Date(rs.getTimestamp("Data").getTime()));
				obj.setHorario(rs.getTime("Horario").toLocalTime());
				obj.setObservacao(rs.getString("Observacao"));

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
	
	public List<Extrato> filteredList(LocalDate dataInicial, LocalDate dataFinal){ //modificar
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM extrato "
					+ " WHERE Data BETWEEN ? AND ? ");
			
			st.setString(1, dataInicial.toString());
			st.setString(2, dataFinal.toString());
					
			rs = st.executeQuery();

			List<Extrato> list = new ArrayList<>();

			while (rs.next()) {

				Extrato obj = new Extrato();
				obj.setId(rs.getInt("Id"));
				obj.setCliente(rs.getString("Cliente"));
				obj.setServico(rs.getString("Servico"));
				obj.setPagamento(rs.getString("Pagamento"));
				obj.setValor(rs.getDouble("Valor"));
				obj.setData(new java.util.Date(rs.getTimestamp("Data").getTime()));
				obj.setHorario(rs.getTime("Horario").toLocalTime());
				obj.setObservacao(rs.getString("Observacao"));

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