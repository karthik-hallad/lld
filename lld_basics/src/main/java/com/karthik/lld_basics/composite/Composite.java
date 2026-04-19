package com.karthik.lld_basics.composite;

import java.util.ArrayList;
import java.util.List;

public class Composite {
  public void run(){
    // === PHASE 1: Construction ===
    // Leaves are declared as CartItem — they have no
    // extra methods beyond the interface, so nothing is
    // lost.
    CartItem book = new Product("Atomic Habits", 499);
    CartItem phone = new Product("iPhone 15", 79999);
    CartItem earbuds = new Product("AirPods", 15999);
    CartItem charger = new Product("20W Charger", 1999);

    // Composites are declared as ProductBundle (concrete
    // type) so we can call addProduct() — a method that
    // only exists on ProductBundle, NOT on CartItem.
    // This is intentional: we need the full type during
    // construction.
    ProductBundle iphoneCombo =
        new ProductBundle("iPhone Essentials Combo");
    iphoneCombo.addProduct(phone);
    iphoneCombo.addProduct(earbuds);
    iphoneCombo.addProduct(charger);

    // Nesting a composite inside another composite.
    // iphoneCombo is a full ProductBundle object in
    // memory. addProduct() accepts CartItem, so
    // iphoneCombo is passed via its CartItem interface.
    // But the OBJECT itself is NOT "compressed" — it
    // still has all ProductBundle methods. The parameter
    // type (CartItem) only limits what addProduct() can
    // see, not what the object actually is.
    ProductBundle iphoneBundleWithschoolKit =
        new ProductBundle("Back to School Kit");
    iphoneBundleWithschoolKit.addProduct(
        new Product("Notebook Pack", 249));
    iphoneBundleWithschoolKit.addProduct(iphoneCombo);

    // === PHASE 2: Uniform usage via CartItem ===
    // Once construction is done, everything goes into a
    // List<CartItem>. From here, we only use the
    // interface methods: getPrice() and display().
    // The extra methods (addProduct) still exist on the
    // objects — we just choose not to use them.
    List<CartItem> cart = new ArrayList<>();
    cart.add(book);
    cart.add(iphoneCombo);
    cart.add(iphoneBundleWithschoolKit);

    System.out.println("Your Amazon Cart:");
    double total = 0;
    for (CartItem item : cart) {
      // Polymorphism: Product.display() prints itself;
      // ProductBundle.display() recursively prints all
      // children. The caller doesn't know or care which.
      item.display("  ");
      total += item.getPrice();
    }

    System.out.println("\nTotal: ₹" + total);
  }
}

interface CartItem {
  double getPrice();
  void display(String indent);
}

// Represents a single product
class Product implements CartItem {
  private String name;
  private double price;

  public Product(String name, double price) {
      this.name = name;
      this.price = price;
  }

  public double getPrice() {
      return price;
  }

  public void display(String indent) {
      System.out.println(indent + "Product: " + name + " - ₹" + price);
  }
}

// Represents a bundle of products
class ProductBundle implements CartItem {
  private String bundleName;
  private List<CartItem> items = new ArrayList<>();

  public ProductBundle(String bundleName) {
      this.bundleName = bundleName;
  }

  public void addProduct(CartItem product) {
      items.add(product);
  }

  public double getPrice() {
      double total = 0;
      for (CartItem item : items) {
          total += item.getPrice();
      }
      return total;
  }

  public void display(String indent) {
      System.out.println(indent + "Bundle: " + bundleName);
      for (CartItem item : items) {
          item.display(indent + "  ");
      }
  }
}
