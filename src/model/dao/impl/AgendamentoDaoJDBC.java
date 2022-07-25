package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.AgendamentoDao;
import model.entities.Agendamento;
import model.entities.Cliente;
import model.entities.Servico;

public class AgendamentoDaoJDBC implements AgendamentoDao {

	private Connection conn;

	public AgendamentoDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Agendamento obj) {

		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO agendamento "
					+ "(Cliente_Id, Servico_Id, Data, Horario, Observacao)" + "VALUES " + "(?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setInt(1, obj.getCliente().getId());
			st.setInt(2, obj.getServico().getId());
			st.setDate(3, new java.sql.Date(obj.getData().getTime()));
			st.setTime(4, Time.valueOf(obj.getHorario()));
			st.setString(5, obj.getObservacao());

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
	public void update(Agendamento obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE agendamento "
					+ "SET Cliente_Id = ?, Servico_Id = ?, Data = ?, Horario = ?, Observacao = ? " + "WHERE id = ?");
			st.setInt(1, obj.getCliente().getId());
			st.setInt(2, obj.getServico().getId());
			st.setDate(3, new java.sql.Date(obj.getData().getTime()));
			st.setTime(4, Time.valueOf(obj.getHorario()));
			st.setString(5, obj.getObservacao());
			st.setInt(6, obj.getId());

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
			st = conn.prepareStatement("DELETE FROM agendamento WHERE Id = ?");

			st.setInt(1, id);

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Agendamento> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT agendamento.*," + "    cliente.*," + "    servico.*"
					+ "    FROM agendamento" + "    JOIN cliente ON cliente.Id = agendamento.Cliente_Id"
					+ "    JOIN servico ON servico.Id = agendamento.Servico_Id"
					+ "    ORDER BY agendamento.Data, agendamento.Horario");

			rs = st.executeQuery();

			List<Agendamento> list = new ArrayList<>();
			Map<Integer, Cliente> mapCliente = new HashMap<>();
			Map<Integer, Servico> mapServico = new HashMap<>();

			while (rs.next()) {

				Cliente cliente = mapCliente.get(rs.getInt("agendamento.Cliente_Id"));

				if (cliente == null) {
					cliente = instantiateCliente(rs);
					mapCliente.put(rs.getInt("agendamento.Cliente_Id"), cliente);
				}

				Servico servico = mapServico.get(rs.getInt("agendamento.Servico_Id"));

				if (servico == null) {
					servico = instantiateServico(rs);
					mapServico.put(rs.getInt("agendamento.Servico_Id"), servico);
				}

				Agendamento agendamento = instantiateAgendamento(rs, servico, cliente);
				list.add(agendamento);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Cliente instantiateCliente(ResultSet rs) throws SQLException {
		Cliente cliente = new Cliente();
		cliente.setId(rs.getInt("cliente.Id"));
		cliente.setNome(rs.getString("cliente.Nome"));
		cliente.setCpf(rs.getString("cliente.Cpf"));
		cliente.setTelefone(rs.getString("cliente.Telefone"));
		cliente.setEndereco(rs.getString("cliente.Endereco"));
		cliente.setEmail(rs.getString("cliente.Email"));
		return cliente;
	}

	private Servico instantiateServico(ResultSet rs) throws SQLException {
		Servico servico = new Servico();
		servico.setId(rs.getInt("servico.Id"));
		servico.setDescricao(rs.getString("servico.Descricao"));
		servico.setValor(rs.getDouble("servico.Valor"));
		return servico;
	}

	private Agendamento instantiateAgendamento(ResultSet rs, Servico servico, Cliente cliente) throws SQLException {
		Agendamento agendamento = new Agendamento();
		agendamento.setId(rs.getInt("agendamento.Id"));
		agendamento.setCliente(cliente);
		agendamento.setServico(servico);
		agendamento.setData(new java.util.Date(rs.getDate("agendamento.Data").getTime()));
		agendamento.setHorario(rs.getTime("agendamento.Horario").toLocalTime());
		agendamento.setObservacao(rs.getString("agendamento.Observacao"));

		return agendamento;
	}
}