package com.advancedb.advancedb.service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.advancedb.advancedb.Mapper.EmployeeMapper;
import com.advancedb.advancedb.Mapper.RequestMapper;
import com.advancedb.advancedb.Mapper.ResponseMapper;
import com.advancedb.advancedb.model.Employee;
import com.advancedb.advancedb.model.EmployeeDTO;
import com.advancedb.advancedb.model.PdfGenerator;
import com.advancedb.advancedb.model.Request;
import com.advancedb.advancedb.model.Response;
import com.advancedb.advancedb.repository.EmployeeRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Service
public class EmployeeService implements Serializable {

    // private static Logger logger =
    // LoggerFactory.getLogger(EmployeeService.class);
    private static final long serialVersionUID = 1L;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    ResponseMapper responseService;

    @Autowired
    RequestMapper requestService;

    @Autowired
    ShadowEmployeeService shadowEmployeeService;

    @Autowired
    PdfGenerator pdfgenerate;

    @Value("${excel.filename.prefix}")
    private String filePrefix;

    @Value("${excel.directory.path}")
    private String directoryPath;

    @Cacheable(value = "employee")
    public Response getAllDetailedEmployees() {
        try {
            // Fetch all employees
            List<Employee> empList = employeeRepository.findAll();
            LinkedHashMap<String, Object> data = new LinkedHashMap<>();

            // Map employee entities to DTOs
            for (Employee emp : empList) {
                EmployeeDTO employeeDTO = employeeMapper.toDTO(emp);
                data.put(emp.getId().toString(), employeeDTO);
            }

            // Create a successful response
            return responseService.createResponse(data, "success", HttpStatus.OK.value(), HttpStatus.OK, "");
        } catch (Exception e) {
            // Create an error response
            return responseService.createResponse(null, "error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST, "");
        }
    }

    @Cacheable(value = "employeeByID", key="#empId")
    public Response getEmployeeById(String empId) {
        try {
            // Fetch all employees
            
            List<Employee> empList = employeeRepository.findByEmpid(empId);
            LinkedHashMap<String, Object> data = new LinkedHashMap<>();

                 // Map employee entities to DTOs
            for (Employee emp : empList) {
                EmployeeDTO employeeDTO = employeeMapper.toDTO(emp);
                data.put(emp.getId().toString(), employeeDTO);
            }
             // Check if the employee list is empty
        if (data.isEmpty()) {
            return responseService.createResponse(null, "error: employee not found", 
                                                  HttpStatus.NOT_FOUND.value(),                                            HttpStatus.NOT_FOUND, "");
        }
            // Create a successful response
            return responseService.createResponse(data, "success", HttpStatus.OK.value(), HttpStatus.OK, "");
        } catch (Exception e) {
            // Create an error response
            return responseService.createResponse(null, "error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST, "");
        }
    }


    public ResponseEntity<Response> addOneEmployee(@Valid Request request) {

        String reqId = request.get_reqid();
        EmployeeDTO empDto = requestService.requestEmployeeDTO(request);

        Employee emp = employeeMapper.toEntity(empDto);
        // employeeValidator.validateEmployee(empDto);
        emp.setCreatedAt(LocalDate.now());
        emp.setUpdatedAt(LocalDate.now());
        employeeRepository.save(emp);

        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("employee", empDto);
        // LogFormat.logApiCall("user", "/employee/add", "success", "200", "OK",
        // empDto.getClientReqId(), request.toString(), "Log Added");
        return new ResponseEntity<>(
                responseService.createResponse(data, "success", HttpStatus.OK.value(), HttpStatus.OK, reqId),
                HttpStatus.OK);
    }

    @CachePut(cacheNames = "updateEmployee", key = "#empid")
    public Response updateEmployee(String empid, @Valid Request request) {
        String reqId = request.get_reqid();

        // Find the existing employee by empid
        Employee existingEmployee = employeeRepository.findByEmpid(empid).get(0);
        if (existingEmployee != null) {
            // Copy employee to shadow storage before updating
            shadowEmployeeService.copyEmployeeToShadowEmployee(existingEmployee);

            // Convert request to EmployeeDTO and map to Employee entity
            EmployeeDTO empDto = requestService.requestEmployeeDTO(request);
            Employee employee = employeeMapper.toEntity(empDto);
            employee.setId(existingEmployee.getId());
            employee.setUpdatedAt(LocalDate.now());

            // Save the updated employee
            employeeRepository.save(employee);

            // Prepare the response data with the updated employee DTO
            LinkedHashMap<String, Object> data = new LinkedHashMap<>();
            data.put("employee", empDto);

            // Return a serializable response object
            return responseService.createResponse(data, "success", HttpStatus.OK.value(), HttpStatus.OK, reqId);
        } else {
            // Return an error response if the employee does not exist
            return responseService.createResponse(null, "error: The empId does not exist.",
                    HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, reqId);
        }
    }

    @Cacheable(value = "employeeByFormat", key = "#type")
    public Object getAllEmployeesInFormat(String type, HttpServletResponse rp) {
        try {
            List<Employee> emplist = employeeRepository.findAll();
            List<List<String>> print = new ArrayList<>();
            for (Employee emp : emplist) {
                List<String> subList = new ArrayList<>();
                subList.add("empid : " + emp.getEmpid());
                subList.add("fname : " + emp.getFullname());
                print.add(subList);
            }

            LinkedHashMap<String, Object> data = new LinkedHashMap<>();
            data.put("employeeList", print);

            if ("json".equalsIgnoreCase(type) || "xml".equalsIgnoreCase(type)) {
                // The media type will be set in the controller
                return responseService.createResponse(data, "success", HttpStatus.OK.value(), HttpStatus.OK, "");
            }

            else if ("xlsx".equalsIgnoreCase(type)) {
                String filename = filePrefix + LocalDateTime.now() + ".xlsx";
                byte[] excelData = createExcelFile(emplist, filename);
                rp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                rp.setHeader("Content-Disposition", "attachment; filename=employee_data.xlsx");
                rp.getOutputStream().write(excelData);
                rp.getOutputStream().flush();
                rp.getOutputStream().close();
                return responseService.createResponse(null, "success", HttpStatus.OK.value(), HttpStatus.OK, "");
            }

            else if ("pdf".equalsIgnoreCase(type)) {
                rp.setContentType("application/pdf");
                rp.setHeader("Content-Disposition", "attachment; filename=employees.pdf");
                pdfgenerate.generate(emplist, rp);
                return null;
            } else {
                return responseService.createResponse(null, "error: Please enter a valid output type!",
                        HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "");
            }
        } catch (Exception e) {
            return responseService.createResponse(null, "error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST, "");
        }
    }

    public byte[] createExcelFile(List<Employee> emplist, String filename) {
        try {
            Workbook workbook = new XSSFWorkbook();
            FileOutputStream fileOut = new FileOutputStream(directoryPath + "/" + filename);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet("Employees");
            int rowNum = 1;

            // Create the header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("EmpId");
            headerRow.createCell(1).setCellValue("Full Name");
            headerRow.createCell(2).setCellValue("First Name");

            for (Employee emp : emplist) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(emp.getEmpid());
                row.createCell(1).setCellValue(emp.getFullname());
                row.createCell(2).setCellValue(emp.getFname());
            }

            workbook.write(out);
            workbook.write(fileOut);
            return out.toByteArray();
        } catch (IOException e) {
            e.getMessage();
            return null;
        }

    }

    @CacheEvict(cacheNames = "deleteEmployee", key = "#empid", beforeInvocation = true)
    public Response deleteEmployee(String empid) {
        // Find the existing employee by empid
        Employee existingEmployee = employeeRepository.findByEmpid(empid).get(0);

        // Copy the employee to the shadow repository before deletion
        shadowEmployeeService.copyEmployeeToShadowEmployee(existingEmployee);

        // Prepare the response data with the deleted employee
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("employee", existingEmployee);

        // Delete the employee from the main repository
        employeeRepository.delete(existingEmployee);

        // Return a serializable response object
        return responseService.createResponse(data, "success", HttpStatus.OK.value(), HttpStatus.OK, "");
    }


// @Cacheable(value = "filteredEmployee" ,key="#filter")
public Response getFilteredEmployees(String filter) {
    try{
    List<Employee> emplist;

    // Retrieve all employees if filter is null or empty
    if (filter == null || "".equals(filter)) {
        emplist = employeeRepository.findAll(); // This should return all employees
    } else {
        emplist = employeeRepository.findEmployeesByFullname(filter);
    }

    // Create a nested list to store employee data
    List<List<String>> print = new ArrayList<>();
    for (Employee emp : emplist) {
        List<String> subList = new ArrayList<>();
        subList.add("empid : " + emp.getEmpid());
        subList.add("fname : " + emp.getFullname());
        print.add(subList);
    }

    // Create a LinkedHashMap to hold the response data
    LinkedHashMap<String, Object> data = new LinkedHashMap<>();
    data.put("employeeList", print);

    // Return a serializable response object
    return responseService.createResponse(data, "success", HttpStatus.OK.value(), HttpStatus.OK, "");
}catch (Exception e) {
    return responseService.createResponse(null, "error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST, "");
}
}



// @Cacheable(value = "employeeByFormat", key = "#type")
public Object getFilteredEmployeesInFormatAndFilter(String filter, String type, HttpServletResponse rp) {
    try {
        List<Employee> emplist;

        // Retrieve all employees if filter is null or empty, otherwise filter by fullname
        if (filter == null || filter.isEmpty()) {
            emplist = employeeRepository.findAll();
        } else {
            emplist = employeeRepository.findEmployeesByFullname(filter);
        }

        // Create a nested list to store employee data
        List<List<String>> print = formatEmployeeList(emplist);

        // Create a LinkedHashMap to hold the response data
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("employeeList", print);

        if ("json".equalsIgnoreCase(type) || "xml".equalsIgnoreCase(type)) {
            // Return JSON or XML response
            return responseService.createResponse(data, "success", HttpStatus.OK.value(), HttpStatus.OK, "");
        } else if ("xlsx".equalsIgnoreCase(type)) {
            // Generate and write Excel file to the response
            String filename = filePrefix + LocalDateTime.now() + ".xlsx";
            byte[] excelData = createExcelFile(emplist, filename);
            rp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            rp.setHeader("Content-Disposition", "attachment; filename=employee_data.xlsx");
            rp.getOutputStream().write(excelData);
            rp.getOutputStream().flush();
            rp.getOutputStream().close();
            return responseService.createResponse(null, "success", HttpStatus.OK.value(), HttpStatus.OK, "");
        } else if ("pdf".equalsIgnoreCase(type)) {
            // Generate and write PDF to the response
            rp.setContentType("application/pdf");
            rp.setHeader("Content-Disposition", "attachment; filename=employees.pdf");
            pdfgenerate.generate(emplist, rp);
            return null;
        } else {
            // Return error for unsupported format type
            return responseService.createResponse(null, "error: Please enter a valid output type!",
                            HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "");
        }
    } catch (Exception e) {
        // Handle exceptions and return an error response
        return responseService.createResponse(null, "error: " + e.getMessage(),
                        HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "");
    }
}

// Utility method to format the employee list
private List<List<String>> formatEmployeeList(List<Employee> emplist) {
    List<List<String>> formattedList = new ArrayList<>();
    for (Employee emp : emplist) {
        List<String> subList = new ArrayList<>();
        subList.add("empid : " + emp.getEmpid());
        subList.add("fname : " + emp.getFullname());
        formattedList.add(subList);
    }
    return formattedList;
}

}

