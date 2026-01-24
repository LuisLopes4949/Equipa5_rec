package com.upt.lp.despesaspessoais.exception;

import java.time.LocalDateTime;

public class ErroResposta {
    private LocalDateTime dataHora;
    private int status;
    private String mensagem;
    private String detalhe;

    public ErroResposta(int status, String mensagem, String detalhe) {
        this.dataHora = LocalDateTime.now();
        this.status = status;
        this.mensagem = mensagem;
        this.detalhe = detalhe;
    }


    public LocalDateTime getDataHora() { 
    	return dataHora; 
    	}
    public int getStatus() { 
    	return status; 
    	}
    public String getMensagem() { 
    	return mensagem; 
    	}
    public String getDetalhe() { 
    	return detalhe; 
    	}
}