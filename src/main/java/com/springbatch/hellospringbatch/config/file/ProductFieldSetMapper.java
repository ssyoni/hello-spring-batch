package com.springbatch.hellospringbatch.config.file;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

// 파일의 라인을 Product의 어떤 필드와 매핑할지에 대한 전략
public class ProductFieldSetMapper implements FieldSetMapper<Product> {
    @Override
    public Product mapFieldSet(FieldSet fieldSet) throws BindException {
        Product product = new Product();
        product.setId(fieldSet.readLong(0));
        product.setName(fieldSet.readString(1));
        product.setPrice(fieldSet.readBigDecimal(2));
        return product;
    }
}
