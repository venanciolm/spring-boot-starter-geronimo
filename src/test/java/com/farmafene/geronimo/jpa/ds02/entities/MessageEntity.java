package com.farmafene.geronimo.jpa.ds02.entities;

import com.farmafene.geronimo.jpa.SequenceUUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "MENSAJE_ME")
public class MessageEntity {
    @Id
    @Column(name = "ME_ID", length = 36, columnDefinition = "CHAR(36)")
    @SequenceUUID
    private String id;
    @Column(name = "ME_MENSAJE", length = 128, columnDefinition = "VARCHAR(128)")
    private String mensaje;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MessageEntity [id=" + id + ", mensaje=" + mensaje + "]";
    }

    public String getMensaje() {
        return mensaje;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((mensaje == null) ? 0 : mensaje.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MessageEntity other = (MessageEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (mensaje == null) {
            if (other.mensaje != null)
                return false;
        } else if (!mensaje.equals(other.mensaje))
            return false;
        return true;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
