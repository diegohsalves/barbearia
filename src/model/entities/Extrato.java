package model.entities;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Extrato {
	
	private Integer id;
	private String cliente;
	private String servico;
	private Double valor;
	private Date data;
	private LocalTime horario;
	private String observacao;
	private String Pagamento;
	
	List<String> opcoesPagamento = Arrays.asList("DINHEIRO", "DEBITO", "CREDITO");
	
	public Extrato() {
		
	}

	public Extrato(Integer id, String cliente, String servico, Double valor, Date data, LocalTime horario, String observacao) {
		this.id = id;
		this.cliente = cliente;
		this.servico = servico;
		this.valor = valor;
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

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getServico() {
		return servico;
	}

	public void setServico(String servico) {
		this.servico = servico;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
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
	
	public String getPagamento() {
		return Pagamento;
	}
	
	public void setPagamento(String Pagamento) {
		this.Pagamento = Pagamento;
	}
	
	public List<String> getOpcoesPagamentoList(){
		return opcoesPagamento;
	}
}
