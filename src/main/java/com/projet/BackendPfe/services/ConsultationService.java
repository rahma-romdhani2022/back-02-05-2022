package com.projet.BackendPfe.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projet.BackendPfe.Entity.Consultation;
import com.projet.BackendPfe.repository.ConsultationRepository;

@Service
public class ConsultationService {
@Autowired ConsultationRepository repository ; 
/******************Oeil Droite *******************/
	public void updateImage1Droite(long id , MultipartFile file) throws IOException {
		 Consultation  consultation = repository.findById(id).get();
		 consultation.setImage1_Droite(compressZLib(file.getBytes()));
		repository.save(consultation);
	}
	public void updateImage2Droite(long id , MultipartFile file) throws IOException {
		 Consultation  consultation = repository.findById(id).get();
		 consultation.setImage2_Droite(compressZLib(file.getBytes()));
		repository.save(consultation);
	}public void updateImage3Droite(long id  , MultipartFile file) throws IOException {
		 Consultation  consultation = repository.findById(id).get();
		 consultation.setImage3_Droite(compressZLib(file.getBytes()));
		repository.save(consultation);
	}public void updateImage4Droite(long id ,MultipartFile file) throws IOException {
		 Consultation  consultation = repository.findById(id).get();
		 consultation.setImage4_Droite(compressZLib(file.getBytes()));
		repository.save(consultation);
	}
	public void updateImage5Droite(long id,MultipartFile file) throws IOException {
		 Consultation  consultation = repository.findById(id).get();
		 consultation.setImage5_Droite(compressZLib(file.getBytes()));
		repository.save(consultation);
	}
	
	/******************* Oeil Gauche ******************/
	public void updateImage1Gauche(long id ,MultipartFile file) throws IOException {
		 Consultation  consultation = repository.findById(id).get();
		 consultation.setImage1_Gauche(compressZLib(file.getBytes()));
		repository.save(consultation);
	}
	public void updateImage2Gauche(long id, MultipartFile file) throws IOException {
		 Consultation  consultation = repository.findById(id).get();
		 consultation.setImage2_Gauche(compressZLib(file.getBytes()));
		repository.save(consultation);
	}public void updateImage3Gauche(long id , MultipartFile file) throws IOException {
		 Consultation  consultation = repository.findById(id).get();
		 consultation.setImage3_Gauche(compressZLib(file.getBytes()));
		repository.save(consultation);
	}public void updateImage4Gauche(long id ,MultipartFile file) throws IOException {
		 Consultation  consultation = repository.findById(id).get();
		 consultation.setImage4_Gauche(compressZLib(file.getBytes()));
		repository.save(consultation);
	}
	public void updateImage5Gauche(long id,MultipartFile file) throws IOException {
		 Consultation  consultation = repository.findById(id).get();
		 consultation.setImage5_Gauche(compressZLib(file.getBytes()));
		repository.save(consultation);
	}
	public static byte[] compressZLib(byte[] data) {
		Deflater deflater = new Deflater();
		deflater.setInput(data);
		deflater.finish();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[2048];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		try {
			outputStream.close();
		} catch (IOException e) {
		}
		System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);

		return outputStream.toByteArray();
	}

	// uncompress the image bytes before returning it to the angular application
	public static byte[] decompressZLib(byte[] data) {
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[2048];
		try {
			while (!inflater.finished()) {
				int count = inflater.inflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			outputStream.close();
		} catch (IOException ioe) {
		} catch (DataFormatException e) {
		}
		return outputStream.toByteArray();
	}
	
}
