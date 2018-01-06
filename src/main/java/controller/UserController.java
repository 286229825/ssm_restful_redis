package controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mapper.UserMapper;
import po.User;

//@RestController可将返回数据都转为json格式，因为它是Controller和Responsebody的组合体
//consumes表示指定处理请求提交的参数类型，对应请求头中的Content-Type参数
//produces表示指定返回参数类型，对应请求头中的Accept参数
@RestController
@RequestMapping(value="/practice/user",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
public class UserController {
	@Autowired
	private UserMapper userMapper;
	
	//method表示指定请求的method类型
	//@PathVariable表示请求地址中的参数映射
	//请求地址中的?后的参数用类似于 Integer id2 的来映射就可以了，不需要其他任何注解
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public List<User> getUsersByCondition(@PathVariable("id") Integer id,Integer id2) throws Exception {
		User user = new User();
		user.setId(id);
		return userMapper.selectByCondition(user);
	}
	
	//在请求体中的参数用@RequestBody来指定其映射的类型
	@RequestMapping(method=RequestMethod.POST)
	public void addUser(@RequestBody User user) throws Exception{
		userMapper.insertOne(user);
	}
	
	//在请求体中的参数用@RequestBody来指定其映射的类型
	@RequestMapping(method=RequestMethod.DELETE)
	public void deleteUser(@RequestBody User user) throws Exception{
		userMapper.deleteOne(user.getId());
	}
	
	//在请求体中的参数用@RequestBody来指定其映射的类型
	@RequestMapping(method=RequestMethod.PUT)
	public void updateUser(@RequestBody User user) throws Exception{
		userMapper.updateOne(user);
	}
	
}
