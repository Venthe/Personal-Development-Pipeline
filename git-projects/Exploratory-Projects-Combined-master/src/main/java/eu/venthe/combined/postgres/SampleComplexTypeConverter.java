package eu.venthe.combined.postgres;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SampleComplexTypeConverter implements AttributeConverter<SampleEntity.SampleNested.SampleComplexType, String> {
    @Override
    public String convertToDatabaseColumn(SampleEntity.SampleNested.SampleComplexType attribute) {
        return attribute.getValue();
    }

    @Override
    public SampleEntity.SampleNested.SampleComplexType convertToEntityAttribute(String dbData) {
        return new SampleEntity.SampleNested.SampleComplexType(dbData);
    }
}
