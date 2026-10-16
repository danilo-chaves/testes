package model;

import com.google.gson.annotations.Expose;
import lombok.Data;

/*@Data Serve para gerar automaticamente os métodos getters e setters para as classes os
campos das classes e criar os métodos toString() que retorna uma variável legível
dos atributos das classes; implementa os métodos equals() e hashCode() com base nos
campos das classes*/
@Data
public class EntregaModel {
    @Expose(serialize = false) //Exclui o campo do processo de serialização Java -> JSON
    private int numeroEntrega;
    @Expose
    private int numeroPedido;
    @Expose
    private String nomeEntregador;
    @Expose
    private String statusEntrega;
    @Expose
    private String dataEntrega;
}
