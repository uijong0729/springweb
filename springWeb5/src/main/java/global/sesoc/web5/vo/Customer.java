package global.sesoc.web5.vo;

/**
 * 회원 정보 VO
 */
public class Customer {
	String custid;				//고객 아이디
	String password;			//비밀번호
	String name;				//고객 이름
	String email;				//고객 이메일
	String division;			//고객구분 : personal(개인), company(기업)
	String idno;				//식별번호 (개인: 주민번호, 법인: 사업자 번호)	
	String address;				//주소
	
	public Customer() {
	}
	
	public Customer(String custid, String password, String name, String email, String division, String idno,
			String address) {
		this.custid = custid;
		this.password = password;
		this.name = name;
		this.email = email;
		this.division = division;
		this.idno = idno;
		this.address = address;
	}
	
	public String getCustid() {
		return custid;
	}
	public void setCustid(String custid) {
		this.custid = custid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getIdno() {
		return idno;
	}
	public void setIdno(String idno) {
		this.idno = idno;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return "Customer [custid=" + custid + ", password=" + password + ", name=" + name + ", email=" + email
				+ ", division=" + division + ", idno=" + idno + ", address=" + address + "]";
	}
}
