package com.github.t1.somemake.pom;

import com.github.t1.somemake.model.Product;
import com.github.t1.xml.XmlElement;

@PomSection(from = "packaging", to = "build")
class PackagingWriter extends PomSectionWriter {
    public PackagingWriter(Product product) {
        super(product);
    }

    @Override
    protected void addTo(XmlElement out) {
        // out.addAttribute("packaging", product.id().idString());
    }
}
