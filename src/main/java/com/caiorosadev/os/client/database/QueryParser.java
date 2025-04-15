package com.caiorosadev.os.client.database;

import com.caiorosadev.os.shared.model.Record;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe responsável por interpretar e executar consultas RAW.
 * Suporta comandos: SELECT, INSERT, UPDATE, DELETE.
 */
public class QueryParser {
	private final DatabaseClientService databaseService;

	public QueryParser(DatabaseClientService databaseService) {
		this.databaseService = databaseService;
	}

	/**
	 * Executa a consulta RAW fornecida.
	 *
	 * @param query Consulta RAW a ser executada.
	 */
	public void executeQuery(String query) {
		if (query == null || query.trim().isEmpty()) {
			System.out.println("Consulta vazia. Tente novamente.");
			return;
		}

		query = query.trim();
		String upperQuery = query.toUpperCase();

		try {
			if (upperQuery.startsWith("SELECT")) {
				executeSelect(query);
			} else if (upperQuery.startsWith("INSERT")) {
				executeInsert(query);
			} else if (upperQuery.startsWith("UPDATE")) {
				executeUpdate(query);
			} else if (upperQuery.startsWith("DELETE")) {
				executeDelete(query);
			} else {
				System.out.println("Comando não reconhecido. Apenas SELECT, INSERT, UPDATE e DELETE são suportados.");
			}
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Erro ao executar a consulta: " + e.getMessage());
		}
	}

	private void executeSelect(String query) throws IOException, ClassNotFoundException {
		if (query.toUpperCase().contains("WHERE")) {
			// Exemplo esperado: SELECT * FROM records WHERE id = 1;
			Pattern pattern = Pattern.compile("(?i)WHERE\\s+id\\s*=\\s*(\\d+)");
			Matcher matcher = pattern.matcher(query);

			if (matcher.find()) {
				int id = Integer.parseInt(matcher.group(1));
				Record record = databaseService.findRecordById(id);

				if (record != null) {
					System.out.println("Registro encontrado: ID=" + record.getId() + ", Nome=" + record.getName());
				} else {
					System.out.println("Nenhum registro encontrado com o ID " + id);
				}
			} else {
				System.out.println("Consulta mal formada. Esperado: 'WHERE id = <número>'");
			}
		} else {
			// Exemplo esperado: SELECT * FROM records;
			List<Record> records = databaseService.listAllRecords();
			System.out.println("Registros encontrados: " + records.size());
			for (Record record : records) {
				System.out.println("ID=" + record.getId() + ", Nome=" + record.getName());
			}
		}
	}

	private void executeInsert(String query) throws IOException, ClassNotFoundException {
		// Exemplo esperado: INSERT INTO records (name) VALUES ('Nome do Registro');
		Pattern pattern = Pattern.compile("(?i)VALUES\\s*\\(\\s*'([^']+)'\\s*\\)");
		Matcher matcher = pattern.matcher(query);

		if (matcher.find()) {
			String name = matcher.group(1);
			Record inserted = databaseService.insertRecord(name);
			if (inserted != null) {
				System.out.println("Registro inserido com sucesso: ID=" + inserted.getId() + ", Nome=" + inserted.getName());
			}
		} else {
			System.out.println("Consulta mal formada. Esperado: VALUES ('nome')");
		}
	}

	private void executeUpdate(String query) throws IOException, ClassNotFoundException {
		// Exemplo esperado: UPDATE records SET name = 'Novo Nome' WHERE id = 1;
		Pattern setPattern = Pattern.compile("(?i)SET\\s+name\\s*=\\s*'([^']+)'");
		Pattern wherePattern = Pattern.compile("(?i)WHERE\\s+id\\s*=\\s*(\\d+)");
		Matcher setMatcher = setPattern.matcher(query);
		Matcher whereMatcher = wherePattern.matcher(query);

		if (setMatcher.find() && whereMatcher.find()) {
			String newName = setMatcher.group(1);
			int id = Integer.parseInt(whereMatcher.group(1));
			Record updated = databaseService.updateRecord(id, newName);
			if (updated != null) {
				System.out.println("Registro atualizado com sucesso: ID=" + updated.getId() + ", Nome=" + updated.getName());
			}
		} else {
			System.out.println("Consulta mal formada. Esperado: SET name='novo nome' e WHERE id=<número>");
		}
	}

	private void executeDelete(String query) throws IOException, ClassNotFoundException {
		// Exemplo esperado: DELETE FROM records WHERE id = 1;
		Pattern pattern = Pattern.compile("(?i)WHERE\\s+id\\s*=\\s*(\\d+)");
		Matcher matcher = pattern.matcher(query);
		if (matcher.find()) {
			int id = Integer.parseInt(matcher.group(1));
			databaseService.deleteRecord(id);
			System.out.println("Registro com ID " + id + " removido com sucesso.");
		} else {
			System.out.println("Consulta mal formada. Esperado: WHERE id = <número>");
		}
	}
}