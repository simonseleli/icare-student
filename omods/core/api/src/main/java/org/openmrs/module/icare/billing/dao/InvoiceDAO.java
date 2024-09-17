package org.openmrs.module.icare.billing.dao;

// Generated Oct 7, 2020 12:49:21 PM by Hibernate Tools 5.2.10.Final

import org.hibernate.Query;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.icare.billing.models.Invoice;
import org.openmrs.module.icare.billing.models.InvoiceItem;
import org.openmrs.module.icare.core.dao.BaseDAO;
import org.openmrs.module.icare.billing.models.DiscountInvoiceItem;

import java.util.Date;
import java.util.List;

/**
 * Home object for domain model class BlInvoice.
 * 
 * @see org.openmrs.module.icare.billing.models.Invoice
 * @author Hibernate Tools
 */
public class InvoiceDAO extends BaseDAO<Invoice> {
	
	DbSessionFactory sessionFactory;
	
	protected DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public List<Invoice> findByPatientUuid(String patientUuid) {
		DbSession session = this.getSession();
		//TODO write query which gets by patientUuid
		//String queryStr = "SELECT i FROM Invoice i WHERE i.visit.patient.uuid = :patientUuid";
		String queryStr = "SELECT invoice FROM Invoice invoice " + "INNER JOIN invoice.visit visit "
		        + "INNER JOIN visit.patient patient " + "WHERE patient.uuid = :patientUuid";
		Query query = session.createQuery(queryStr);
		query.setParameter("patientUuid", patientUuid);
		
		/*String queryStr = "SELECT invoice FROM Invoice invoice " +
				"INNER JOIN invoice.visit visit ";
		Query query = session.createQuery(queryStr);*/
		
		//		String queryStr2 = "SELECT i FROM Invoice i WHERE i.visit.patient.uuid = :patientUuid";
		//		Query query2 = session.createQuery(queryStr2);
		//		query2.setParameter("patientUuid", patientUuid);
		return query.list();
	}
	
	public List<Invoice> findByPatientUuidAndPending(String patientUuid) {
		DbSession session = this.getSession();
		String queryStr = "SELECT invoice FROM Invoice invoice WHERE \n" + "invoice.visit.patient.uuid = :patientUuid \n"
		        + "AND (SELECT SUM(item.price*item.quantity) FROM InvoiceItem item WHERE item.id.invoice = invoice) \n"
		        + "> ((SELECT CASE WHEN SUM(pi.amount) IS NULL THEN 0 ELSE SUM(pi.amount) END FROM " + "PaymentItem pi "
		        +
		        //"INNER JOIN PaymentItem pi ON(pi.id.payment=payment) " +
		        "WHERE pi.id.payment.invoice = invoice)+(SELECT CASE WHEN SUM(di.amount) IS NULL THEN 0 ELSE SUM(di.amount) END FROM "
		        + "DiscountInvoiceItem di " + " WHERE di.id.invoice = invoice))";
		//queryStr = "SELECT i FROM Invoice i WHERE i.patient.uuid = :patientUuid";
		Query query = session.createQuery(queryStr);
		query.setParameter("patientUuid", patientUuid);
		return query.list();
	}
	
	public List<Invoice> findAllInvoiceByPatientUuid(String patientUuid) {
		DbSession session = this.getSession();
		//TODO write query which gets by patientUuid
		String queryStr = "SELECT i FROM Invoice i WHERE i.visit.patient.uuid = :patientUuid";
		Query query = session.createQuery(queryStr);
		query.setParameter("patientUuid", patientUuid);
		return query.list();
	}
	
	public InvoiceItem getInvoiceItemByOrderUuid(String orderUuid) {
		DbSession session = this.getSession();
		//TODO write query which gets by patientUuid
		String queryStr = "SELECT i FROM InvoiceItem i WHERE i.id.order.uuid = :orderUuid";
		Query query = session.createQuery(queryStr);
		query.setParameter("orderUuid", orderUuid);
		return (InvoiceItem) query.uniqueResult();
	}
	
	public List<Invoice> findByVisitUuidAndPending(String visitUuid) {
		DbSession session = this.getSession();
		String queryStr = "SELECT invoice FROM Invoice invoice WHERE \n" + "invoice.visit.uuid = :visitUuid \n"
		        + "AND (SELECT SUM(item.price*item.quantity) FROM InvoiceItem item WHERE item.id.invoice = invoice) \n"
		        + "> (SELECT CASE WHEN SUM(pi.amount) IS NULL THEN 0 ELSE SUM(pi.amount) END FROM " + "PaymentItem pi " +
		        //"INNER JOIN PaymentItem pi ON(pi.id.payment=payment) " +
		        "WHERE pi.id.payment.invoice = invoice)";
		//queryStr = "SELECT i FROM Invoice i WHERE i.patient.uuid = :patientUuid";
		Query query = session.createQuery(queryStr);
		query.setParameter("visitUuid", visitUuid);
		return query.list();
	}

	public List<Object[]> getTotalAmountFromPaidInvoices(Date startDate, Date endDate, String provider) {
		DbSession session = this.getSession();
		new InvoiceItem();
		String queryStr = "SELECT SUM(inv.price * inv.quantity) as total, inv.id FROM InvoiceItem inv ";
		if (startDate != null && endDate != null) {
			queryStr += " WHERE inv.id.order.dataActivated BETWEEN :startDate AND :endDate ";
		}
		if (provider != null) {
			if (queryStr.contains("WHERE")) {
				queryStr += " AND inv.id.order.orderer.uuid = :provider ";
			} else {
				queryStr += " WHERE inv.id.order.orderer.uuid = :provider ";
			}
		}
		queryStr += " GROUP BY inv.id";
		Query query = session.createQuery(queryStr);
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		 if (provider != null) {
			 query.setParameter("provider", provider);
		 }
		return query.list();
	}
}
