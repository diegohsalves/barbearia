package model.entities;

import java.time.LocalTime;
import java.util.Date;

public class Agendamento {
	
	private Integer id;
	private Cliente cliente;
	private Servico servico;
	private Date data;
	private LocalTime horario;
	private String observacao;
	
	public Agendamento() {
		
	}

	public Agendamento(Integer id, Cliente cliente, Servico servico, Date data, LocalTime horario, String observacao) {
		this.id = id;
		this.cliente = cliente;
		this.servico = servico;
		this.data = data;
		this.horario = horario;
		this.observacao = observacao;

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public LocalTime getHorario() {
		return horario;
	}
	
	public void setHorario(LocalTime horario) {
		this.horario = horario;
	}
}
