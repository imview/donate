package com.donate.service.enums;

public enum PayStatusEnum {
	PROCESSING("PROCESSING",0),SUCCESS("SUCCESS", 1), FAIL("FAIL", 2); 
	PayStatusEnum(){};
    PayStatusEnum(String text, Integer  value){
		this.value=value;
		this.text=text;
	}
    
    private Integer value;
	private String text;

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
