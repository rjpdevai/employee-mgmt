package com.rcom.employeemgmt.service;

import com.rcom.employeemgmt.exception.UserNotFoundException;
import com.rcom.employeemgmt.model.Employee;
import com.rcom.employeemgmt.repo.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final Path root = Paths.get("uploads");

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {

        this.employeeRepository = employeeRepository;
        try {
            Files.createDirectories(root);
        }catch (IOException e){
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public Employee addEmployee(Employee employee){
        employee.setEmployeeCode(UUID.randomUUID().toString());
        return employeeRepository.save(employee);
    }

    public List<Employee> findAllEmployees(){
        return employeeRepository.findAll();
    }

    public Employee updateEmployee(Employee employee){
        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(Long id){
        employeeRepository.deleteEmployeeById(id);
    }

    public Employee findEmployeeById(Long id){
        return employeeRepository.findEmployeeById(id)
                .orElseThrow(()->new UserNotFoundException("User not found!"));
    }

    public String uploadFile(MultipartFile file){
        UUID fileUUID = UUID.randomUUID();
        try {
            Files.copy(file.getInputStream(), this.root.resolve(fileUUID.toString()));
            return fileUUID.toString();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

//        try{
//            Path uploadPath = Paths.get("uploads");
//            if (!Files.exists(uploadPath)) {
//                Files.createDirectories(uploadPath);
//            }
//            byte[] bytes = file.getBytes();
//            Files.write(uploadPath.resolve(Objects.requireNonNull(file.getOriginalFilename())), bytes);
//            //return new ResponseEntity<>(gson.toJson("File uploaded successfully"), HttpStatus.OK);
//        }catch (IOException e){
//            e.printStackTrace();
//            //return new ResponseEntity<>(gson.toJson("Upload failed"), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
    }

    public Resource getImage(String fileName){
        Path file = root.resolve(fileName);
        try {
            Resource resource = new UrlResource((file.toUri()));
            if(resource.exists() || resource.isReadable()){
                return resource;
            }else {
                throw new RuntimeException(("Image not found!"));
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
