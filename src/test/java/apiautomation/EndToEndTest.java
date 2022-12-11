package apiautomation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EndToEndTest {
	
	Response response;
	RequestSpecification request;
	int employeeid;
	JsonPath jpath;
	Map<String,Object>Mapobject;

	public Response GetAllEmployees()
	{
		RestAssured.baseURI="http://localhost:3000";
		request=RestAssured.given();
		response=request.get("employees");
		return response;
	}
	
	public Response GetSingleEmployee(int empdid)
	{
		RestAssured.baseURI="http://localhost:3000";
		request=RestAssured.given();
		response=request.get("employees/"+empdid);
		return response;
	
	}
	
	public Response CreateEmployee(String empName,String empSalary)
	{
		Mapobject=new HashMap<String,Object>();
		Mapobject.put("name", empName);
		Mapobject.put("salary", empSalary);
		RestAssured.baseURI="http://localhost:3000";
		request=RestAssured.given();
		response=request.contentType(ContentType.JSON).accept(ContentType.JSON).body(Mapobject).
		post("employees/create");
		return response;
	}
	
	public Response UpdateEmployee(String name, String salary, int empdid)
	{
		Mapobject=new HashMap<String,Object>();
		Mapobject.put("name", "Smith");
		Mapobject.put("salary", "3500");
		RestAssured.baseURI="http://localhost:3000";
		request=RestAssured.given();
		response=request.contentType(ContentType.JSON).accept(ContentType.JSON).body(Mapobject)
		.put("employees/"+empdid);
		return response;
	}
	
	public Response DeleteEmployee(int empdid)
	{
		RestAssured.baseURI="http://localhost:3000";
		request=RestAssured.given();
		response=request.delete("employees/"+empdid);
		return response;
	}
	
	@Test(priority=0)
	public void TestGetAllEmployess()
	{
		response=GetAllEmployees();
		Assert.assertEquals(200,response.getStatusCode());
		
	}
	
	@Test(priority=1)
	public void TestCreateEmployee()
	{
		
		response=CreateEmployee("John", "8000");
		Assert.assertEquals(201,response.getStatusCode());
		jpath=response.jsonPath();
		employeeid=jpath.get("id");
	}
	
	@Test(priority=2)
	public void TestGetSingleEmployee()
	{
				
		response=GetSingleEmployee(employeeid);
		jpath=response.jsonPath();
		Assert.assertEquals("John", jpath.get("name"));
		Assert.assertEquals(200,response.getStatusCode());
	}
	
	@Test(priority=3)
		public void TestUpdateEmployee()
		{
		response=UpdateEmployee("Smith","8000", employeeid);
		Assert.assertEquals(200,response.getStatusCode());
		}
	
	@Test(priority=4)
	public void VerifyEmployeeAfterUpdate()
	{
		response=GetSingleEmployee(employeeid);
		Assert.assertEquals(200,response.getStatusCode());
		//fetch name and verify name is smith
		jpath=response.jsonPath();
		Assert.assertEquals("Smith", jpath.get("name"));
	}
	

		@Test(priority=5)
		public void TestDeleteEmployee()
		{
			
		response=DeleteEmployee(employeeid);
		Assert.assertEquals(200,response.getStatusCode());
		}
		
		@Test(priority=6)
		public void VerifyEmployeeIsDeleted()
		{
		response=GetSingleEmployee(employeeid);
		Assert.assertEquals(404,response.getStatusCode());
		}
		
		
		@Test(priority=7)
		public void VerifyEmployeeIsDeletedFromList()
		{
		response=GetAllEmployees();
		jpath=response.jsonPath();
		List<String>names=jpath.get("name");
		
		Assert.assertFalse(names.contains("Smith"));
		
	}
}
