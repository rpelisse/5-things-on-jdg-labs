package org.jboss.as.labs.datagrid.lab4;

import java.io.Serializable;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Indexed
class Movie implements Serializable {

	private static final long serialVersionUID = 4575532288121468004L;

	public  Movie(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	private long id;
	
    @Field(index = Index.YES, analyze = Analyze.NO,  store = Store.NO)
    private String name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Movie [id=" + id + ", name=" + name + "]";
	}
 }
