package com.startup.ParkingLocationFinder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ParkingLocationFinderApplicationTests {

	@Test
	public void contextLoads() {
		
		Assert.assertTrue(findUniqueString("Arvind"));
		Assert.assertFalse(findUniqueString("Hemamalini"));
		String s = "Arvind";
		Assert.assertEquals("dnivrA", revStr(s));
		
		Assert.assertEquals("Hemalin",removeDuplicates("Hemamalini"));
	}
	
	public Boolean findUniqueString(String s) {
		boolean[] strFinder = new boolean[256];
		for(int i =0 ; i<s.length();i++) {
			int value = s.charAt(i);
			if(strFinder[value]) return false;
			else
				strFinder[value] = true;
		}
		
		return true;
	}
	
	
	private String revStr(String s) {
		
		Stack<Character> st = new Stack<>();
		
		for(int i =0 ; i<s.length();i++) {
		st.push(s.charAt(i));
		}
		
		StringBuffer sb = new StringBuffer();
		while(!st.isEmpty()) {
			sb.append(st.pop());
		}
		
		return sb.toString();
	}
	
	private String removeDuplicates(String s) {
		
		Set<Character> ll = new LinkedHashSet<>();
		for(int i =0 ; i<s.length();i++) {
			ll.add(s.charAt(i));
			}
		
		Iterator<Character> itr = ll.iterator();
		StringBuffer str = new StringBuffer();
		while(itr.hasNext())
		{
			str.append(itr.next());
		}
	
		return str.toString();
	}

}
