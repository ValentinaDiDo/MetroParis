package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.Connessione;
import it.polito.tdp.metroparis.model.CoppiaID;
import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {

	public List<Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	public List<Linea> getAllLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}

	//CERCO SE ESISTE UNA CONNESSIONE TRA DUE STAZIONI
	public boolean isFermateConnesse(Fermata partenza, Fermata arrivo) {
		
		String sql ="SELECT count(*) AS cnt "
				+ "FROM connessione "
				+ "WHERE id_stazP=? AND id_stazA=? ";
		
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getIdFermata());
			st.setInt(2, arrivo.getIdFermata());
			ResultSet res = st.executeQuery();
			res.first();
			int count = res.getInt("cnt");
			conn.close();
			return count>0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("errore database", e);
		}
	}

	public List<Integer> getIdFermateConnesse(Fermata partenza){
		String sql = "SELECT id_stazA "+
				"FROM connessione "+
				"WHERE id_stazP = ? "+
				"GROUP BY id_stazA ";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getIdFermata());
			ResultSet rs = st.executeQuery();
			List<Integer> fermate = new ArrayList<Integer>();
			
			
			while(rs.next()) {
				fermate.add(rs.getInt("id_stazA"));
			}
			
			return fermate;
		}catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("errore database", e);
		}
	}
	//METODO ANALOGO A GET ALL FERMATE MA RELATIVO ALLE FERMATE E NONO SOLO AGLI ID
	public List<Fermata> getFermateConnesse(Fermata partenza){
		String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata WHERE id_fermata IN (SELECT id_stazA FROM connessione WHERE id_stazP = ? GROUP BY id_stazA) ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getIdFermata());
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_fermata"), rs.getString("nome"), new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
				//SE IL DAO AVESSE LA IDENTITY MAP, EVITEREBBE DI FARE TUTTI QUESTI CONTROLLI PER AGGIUNGERE OGNI ELEMENTO
			}
			st.close();
			conn.close();
			return fermate;
		}catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("errore database", e);
		}
	
	}
	
	//METODO 3 PRENDO DIRETTAMENTE TUTTE LE COPPIE DI FERMATE
	public List<CoppiaID> getAllFermateConnesse(){
		List<CoppiaID> coppie = new ArrayList<CoppiaID>();
		String sql = "SELECT DISTINCT id_stazP, id_stazA "
				+ "FROM connessione ";
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				coppie.add(new CoppiaID(rs.getInt("id_stazP"), rs.getInt("id_stazA")));	  
			}
			conn.close();
			return coppie;
		}catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("errore database", e);
		}
	}
	

}
