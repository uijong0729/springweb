package global.sesoc.web5.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import global.sesoc.web5.vo.Customer;

/**
 * 회원 관련 DAO
 */
@Repository
public class CustomerDAO {
	@Autowired
	SqlSession sqlSession;
	
	/**
	 * 회원 가입 처리
	 * @param customer 사용자가 입력한 가입 정보
	 */
	public int insert(Customer customer) {
		CustomerMapper mapper = sqlSession.getMapper(CustomerMapper.class);
		
		int result = 0;
		try {
			result = mapper.insertCustomer(customer);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * ID로 회원정보 검색
	 * @param id 검색할 아이디
	 * @return 검색된 회원정보. 없으면 null.
	 */
	public Customer get(String id) {
		CustomerMapper mapper = sqlSession.getMapper(CustomerMapper.class);
		Customer c = mapper.selectCustomer(id);
		return c;
	}
	
	/**
	 * 회원 정보 수정 처리
	 * @param customer 사용자가 입력한 수정 정보
	 */
	public int update(Customer customer) {
		CustomerMapper mapper = sqlSession.getMapper(CustomerMapper.class);
		
		int result = 0;
		try {
			result = mapper.updateCustomer(customer);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
