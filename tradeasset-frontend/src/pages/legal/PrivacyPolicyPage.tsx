/**
 * Privacy Policy Page
 * Privacy and data protection policy for TradeSphere
 */

import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowLeft, Lock } from 'lucide-react';
import { Card, CardBody, Button } from '../../components/ui';
import { ROUTES } from '../../config/constants';
import './LegalPages.css';

export const PrivacyPolicyPage: React.FC = () => {
  return (
    <div className="legal-page-container">
      <div className="legal-page-wrapper">
        <div className="legal-page-header">
          <Link to={ROUTES.LOGIN} className="legal-back-link">
            <ArrowLeft size={20} />
            <span>Back to Login</span>
          </Link>
        </div>

        <Card variant="elevated" padding="lg" className="legal-card">
          <div className="legal-title">
            <Lock size={32} />
            <h1>Privacy Policy</h1>
          </div>
          <div className="legal-subtitle">Last Updated: July 21, 2026</div>
          
          <CardBody>
            <div className="legal-content">
              <section className="legal-section">
                <h2>1. Introduction</h2>
                <p>
                  TradeSphere Inc. ("we", "our", or "us") is committed to protecting your privacy. This Privacy
                  Policy explains how we collect, use, disclose, and safeguard your information when you use
                  our trade asset management platform ("Service"). Please read this privacy policy carefully.
                </p>
              </section>

              <section className="legal-section">
                <h2>2. Information We Collect</h2>
                <h3>2.1 Personal Information</h3>
                <p>We collect information that identifies you as an individual, including:</p>
                <ul>
                  <li>Name and contact information (email address, phone number)</li>
                  <li>Account credentials (username and password)</li>
                  <li>Professional information (company, role, department)</li>
                  <li>Financial and transaction data related to trade finance operations</li>
                  <li>Communication records and correspondence</li>
                </ul>

                <h3>2.2 Technical Information</h3>
                <p>We automatically collect certain information when you use our Service:</p>
                <ul>
                  <li>Device information (IP address, browser type, operating system)</li>
                  <li>Usage data (pages visited, time spent, features used)</li>
                  <li>Log files and analytics data</li>
                  <li>Cookies and similar tracking technologies</li>
                </ul>

                <h3>2.3 Financial Transaction Data</h3>
                <p>
                  As a trade finance platform, we process sensitive financial information including letter of
                  credit details, bank guarantee information, payment instructions, and related documentation.
                </p>
              </section>

              <section className="legal-section">
                <h2>3. How We Use Your Information</h2>
                <p>We use the collected information for the following purposes:</p>
                <ul>
                  <li>To provide, operate, and maintain our Service</li>
                  <li>To process and manage trade finance transactions</li>
                  <li>To authenticate users and prevent fraud</li>
                  <li>To send administrative information and service updates</li>
                  <li>To respond to your inquiries and provide customer support</li>
                  <li>To comply with legal obligations and regulatory requirements</li>
                  <li>To improve and optimize our Service through analytics</li>
                  <li>To detect, prevent, and address technical issues</li>
                </ul>
              </section>

              <section className="legal-section">
                <h2>4. Data Security</h2>
                <p>
                  We implement robust security measures to protect your information:
                </p>
                <ul>
                  <li><strong>Encryption:</strong> All data transmitted is encrypted using SSL/TLS protocols</li>
                  <li><strong>Access Controls:</strong> Strict authentication and authorization mechanisms</li>
                  <li><strong>Multi-Factor Authentication:</strong> Optional MFA for enhanced account security</li>
                  <li><strong>Regular Audits:</strong> Periodic security assessments and vulnerability testing</li>
                  <li><strong>Data Backup:</strong> Regular backups with secure storage</li>
                  <li><strong>Compliance:</strong> Adherence to industry standards (ISO 27001, PCI DSS)</li>
                </ul>
                <p>
                  However, no method of transmission over the Internet or electronic storage is 100% secure.
                  While we strive to use commercially acceptable means to protect your information, we cannot
                  guarantee its absolute security.
                </p>
              </section>

              <section className="legal-section">
                <h2>5. Data Sharing and Disclosure</h2>
                <h3>5.1 Third-Party Service Providers</h3>
                <p>
                  We may share your information with third-party service providers who perform services on our
                  behalf, such as:
                </p>
                <ul>
                  <li>Cloud hosting providers</li>
                  <li>Payment processors</li>
                  <li>Analytics services</li>
                  <li>Customer support tools</li>
                </ul>

                <h3>5.2 Legal Requirements</h3>
                <p>We may disclose your information if required to do so by law or in response to:</p>
                <ul>
                  <li>Valid legal requests by public authorities</li>
                  <li>Court orders or subpoenas</li>
                  <li>Regulatory investigations</li>
                  <li>Protection of our rights and safety</li>
                </ul>

                <h3>5.3 Business Transfers</h3>
                <p>
                  In the event of a merger, acquisition, or sale of assets, your information may be transferred
                  as part of that transaction.
                </p>
              </section>

              <section className="legal-section">
                <h2>6. Data Retention</h2>
                <p>
                  We retain your information for as long as necessary to provide our Service and comply with
                  legal obligations. Financial transaction records are retained in accordance with applicable
                  banking and financial regulations, typically for a minimum of 7 years.
                </p>
              </section>

              <section className="legal-section">
                <h2>7. Your Privacy Rights</h2>
                <p>Depending on your location, you may have the following rights:</p>
                <ul>
                  <li><strong>Access:</strong> Request access to your personal information</li>
                  <li><strong>Correction:</strong> Request correction of inaccurate data</li>
                  <li><strong>Deletion:</strong> Request deletion of your personal information</li>
                  <li><strong>Portability:</strong> Request transfer of your data</li>
                  <li><strong>Objection:</strong> Object to processing of your information</li>
                  <li><strong>Restriction:</strong> Request restriction of processing</li>
                  <li><strong>Withdrawal:</strong> Withdraw consent where processing is based on consent</li>
                </ul>
                <p>
                  To exercise these rights, please contact us at privacy@tradesphere.com
                </p>
              </section>

              <section className="legal-section">
                <h2>8. Cookies and Tracking Technologies</h2>
                <p>
                  We use cookies and similar tracking technologies to track activity on our Service. You can
                  instruct your browser to refuse all cookies or to indicate when a cookie is being sent.
                  However, if you do not accept cookies, you may not be able to use some portions of our Service.
                </p>
                <p>Types of cookies we use:</p>
                <ul>
                  <li><strong>Essential Cookies:</strong> Required for the Service to function</li>
                  <li><strong>Analytics Cookies:</strong> Help us understand usage patterns</li>
                  <li><strong>Preference Cookies:</strong> Remember your settings and preferences</li>
                </ul>
              </section>

              <section className="legal-section">
                <h2>9. International Data Transfers</h2>
                <p>
                  Your information may be transferred to and maintained on servers located outside of your
                  state, province, country, or other governmental jurisdiction where data protection laws may
                  differ. We ensure appropriate safeguards are in place for such transfers.
                </p>
              </section>

              <section className="legal-section">
                <h2>10. Children's Privacy</h2>
                <p>
                  Our Service is not directed to individuals under the age of 18. We do not knowingly collect
                  personal information from children. If you become aware that a child has provided us with
                  personal information, please contact us immediately.
                </p>
              </section>

              <section className="legal-section">
                <h2>11. Changes to This Privacy Policy</h2>
                <p>
                  We may update our Privacy Policy from time to time. We will notify you of any changes by
                  posting the new Privacy Policy on this page and updating the "Last Updated" date. You are
                  advised to review this Privacy Policy periodically for any changes.
                </p>
              </section>

              <section className="legal-section">
                <h2>12. Compliance with Regulations</h2>
                <p>
                  We comply with applicable data protection regulations including:
                </p>
                <ul>
                  <li>General Data Protection Regulation (GDPR) for EU users</li>
                  <li>California Consumer Privacy Act (CCPA) for California residents</li>
                  <li>Banking secrecy and financial privacy regulations</li>
                  <li>Industry-specific compliance requirements (Basel III, SWIFT standards)</li>
                </ul>
              </section>

              <section className="legal-section">
                <h2>13. Contact Us</h2>
                <p>
                  If you have any questions about this Privacy Policy or our data practices, please contact us:
                </p>
                <div className="contact-info">
                  <p><strong>Privacy Officer:</strong> privacy@tradesphere.com</p>
                  <p><strong>Email:</strong> support@tradesphere.com</p>
                  <p><strong>Address:</strong> TradeSphere Inc., 123 Finance Street, New York, NY 10001, USA</p>
                  <p><strong>Phone:</strong> +1 (555) 123-4567</p>
                </div>
              </section>
            </div>

            <div className="legal-actions">
              <Link to={ROUTES.LOGIN}>
                <Button variant="primary" size="lg">
                  I Understand
                </Button>
              </Link>
            </div>
          </CardBody>
        </Card>
      </div>
    </div>
  );
};

