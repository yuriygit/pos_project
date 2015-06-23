package com.yuriyb.pointofsale.devices;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.yuriyb.pointofsale.exceptions.InvalidBarCodeException;
import com.yuriyb.pointofsale.exceptions.ProductNotFoundException;
import com.yuriyb.pointofsale.productprices.IProductsInfoDB;

/**
 * PointOfSaleTerminal class. It contains methods for products scanning, calculation of prices etc.
 * @version 1.80 12 April 2015
 * @author  Yuriy B.
 */
public class BarCodesScaner implements IScanner {
	private IProductsInfoDB productsInfoDB;
	private List<String> shoppingCart =  new ArrayList<String>();
	
	public List<String> getShoppingCart() {
		return shoppingCart;
	}

	public void setShoppingCart(List<String> shoppingCart) {
		this.shoppingCart = shoppingCart;
	}
	

	/**
     * Sets pricing 
     *
     * @param productsInfo  storage of products with prices to work with
     */
	public void setProductsPrices(IProductsInfoDB productsInfoDB) {
		this.productsInfoDB = productsInfoDB;
	}
	
	/**
     * Scans products codes
     *
     * @param productCode  product code 
	 * @throws InvalidBarCodeException 
	 * @throws ProductNotFoundException 
     */
	public void scan(String productCode) throws InvalidBarCodeException, ProductNotFoundException {
		if ((null == productCode) || (productCode == "")) {
			throw new InvalidBarCodeException("Invalid bar-code");
		} else if (!productsInfoDB.isPresentInStorage(productCode)) {
			throw new ProductNotFoundException("Product not found");
		} else {
			shoppingCart.add(productCode);
		}
	}

	/**
     * Clears the shoppingCart with products
     */
	public void clearScanned() {
		shoppingCart.clear();
	}
	
	/**
     * Gets total price of products in the shopping cart
     */
	public BigDecimal calculateTotalPrice(){
		
		BigDecimal totalProductsPrice = new BigDecimal(0.00);
		
		for (String itemCode : shoppingCart){	
			BigDecimal productPrice = null;
			productPrice = productsInfoDB.getPrice(itemCode).getValue();
			totalProductsPrice = totalProductsPrice.add(productPrice);
		}
		return totalProductsPrice;
	}
	
	public Map<String,String> getReceipt(){
		
		Map<String, String> resultReceipt = new HashMap<String, String>();
		StringBuffer receiptBuffer = new StringBuffer();
		int itemNumber = 1;
		
		for(String itemCode: shoppingCart){
			BigDecimal productPrice = null;
			String productName = null;
			productName = productsInfoDB.getProductTitle(itemCode);
			productPrice = productsInfoDB.getPrice(itemCode).getValue();
			receiptBuffer.append(itemNumber+","+productName+",price:"+productPrice+";");
			++itemNumber;
		}
		
		BigDecimal totalProductsPrice = calculateTotalPrice();
		resultReceipt.put("boughtProducts",receiptBuffer.toString());
		resultReceipt.put("totalPrice","Total Price:"+totalProductsPrice.toString()+";");
		clearScanned();
		return resultReceipt;
	}	
}
