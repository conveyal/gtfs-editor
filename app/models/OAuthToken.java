package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.transit.Agency;
import play.db.jpa.Model;

@Entity
public class OAuthToken extends Model {
    public String token;
    public Long creationDate;
    @ManyToOne
    public Agency agency;
}
