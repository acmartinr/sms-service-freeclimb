import React, {Component} from 'react';

import './Phones.css';
import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';
// import RemoveModal from '../common/RemoveModal';

import PhoneDetailsModal from './PhoneDetailsModal';
import EditPhoneModal from './EditPhoneModal';
import EditPhonesForwardingModal from './EditPhonesForwardingModal';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';
import Delete from '@material-ui/icons/Delete';
import Button from '@material-ui/core/Button';
import Edit from '@material-ui/icons/Edit';

class Phones extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selectedPhone: {},
      phones: [],
      count: 0,
      rowsPerPage: 10,
      page: 0,
      showAddPhoneModal: false,
      showRemovePhoneModal: false,
      showEditPhoneModal: false,
      showBulkForwardingModal: false,
      bulkForwardingEnabled: false
    };

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
    this.showAddPhoneModal = this.showAddPhoneModal.bind(this);
    this.closeEditModal = this.closeEditModal.bind(this);
    this.removeSelectedPhone = this.removeSelectedPhone.bind(this);
    this.closeRemoveModal = this.closeRemoveModal.bind(this);
    this.removePhone = this.removePhone.bind(this);
    this.closeEditPhoneModal = this.closeEditPhoneModal.bind(this);
    this.closeBulkForwardingModal = this.closeBulkForwardingModal.bind(this);
  }

  updateUserSettings() {
    var that = this;

    CampaignsAPI.getUserUISettings({id: Session.getUser().id}, function (response) {
      if (response.status === 'OK') {
        if (response.data) {
          that.setState({
            bulkForwardingEnabled: response.data.find(setting => setting.skey.includes("phones.bulk.forward.enabled")).sval
          });

          Session.updateUserProperty('bulkForwardingEnabled', response.data.find(setting => setting.skey.includes("phones.bulk.forward.enabled")).sval);
        }
      }
    });
  }

  removeSelectedPhone(phone) {
    CampaignsAPI.removePhone(phone, response => {
      if (response.status === 'OK') {
        this.closeRemoveModal();
        this.updatePhones();
      }
    });
  }

  removePhone(phone) {
    // this.setState({
    //   selectedPhone: phone,
    //   showRemovePhoneModal: true,
    //   title: 'Remove phone',
    //   message: 'Are you sure you want to remove this phone?'
    // })

    this.removeSelectedPhone(phone)
  }

  showBulkForwardingModal() {
    this.setState({showBulkForwardingModal: true});
  }

  closeBulkForwardingModal() {
    this.setState({showBulkForwardingModal: false});
    this.updatePhones();
  }

  closeRemoveModal(phone) {
    this.setState({selectedPhone: {}, showRemovePhoneModal: false})
  }

  showAddPhoneModal() {
    this.setState({showAddPhoneModal: true});
  }

  closeEditModal() {
    this.setState({selectedPhone: {}, showAddPhoneModal: false, page: 0});
    this.updatePhones();
  }

  showEditPhoneModal(phone) {
    this.setState({selectedPhone: phone, showEditPhoneModal: true});
  }

  closeEditPhoneModal() {
    this.setState({selectedPhone: {}, showEditPhoneModal: false});
    this.updatePhones();
  }

  componentDidMount() {
    this.updatePhones();
    this.updateUserSettings();
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updatePhones);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updatePhones);
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    } else {
      return 'empty';
    }
  }

  updatePhones() {
    const request = {
      userId: Session.getUser().id,
      page: this.state.page,
      limit: this.state.rowsPerPage,
      search: this.state.search
    };

    CampaignsAPI.getPhones(request,
      response => {
        if (response.status === "OK") {
          this.setState({
            phones: response.data.phones,
            count: response.data.count
          });
        }
      });
  }

  render() {
    return (
      <div className='padding-top-10'>
        <Button
          type="button"
          variant="contained"
          color="primary"
          onClick={(e) => this.showAddPhoneModal()}
          className="right">Add New Phones</Button>

        {this.state.bulkForwardingEnabled === '1' &&
        <Button
          type="button"
          variant="contained"
          color="primary"
          onClick={(e) => this.showBulkForwardingModal()}
          className="right margin-right-10">Bulk Forwarding</Button>
        }

        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Phone</TableCell>
              <TableCell>Forwarding phone</TableCell>
              <TableCell>Note</TableCell>
              <TableCell>Sent</TableCell>
              <TableCell>Day sent</TableCell>
              <TableCell>In/Out Ratio</TableCell>
              <TableCell>Date</TableCell>
              <TableCell className='action-cell'>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.phones.map(row => (
              <TableRow key={row.id}>
                <TableCell>+{row.phone}</TableCell>
                <TableCell>{row.forwarding ? "+" + row.forwarding : ""}</TableCell>
                <TableCell>{row.note}</TableCell>
                <TableCell>{row.sentCount}</TableCell>
                <TableCell>{row.daySentCount}</TableCell>
                <TableCell>{(row.sentCount > 0 ? (row.inboundCount * 100.0 / row.sentCount) : 0).toFixed(0)}%</TableCell>
                <TableCell>{this.prettyDate(row.date)}</TableCell>
                <TableCell>
                  <Edit
                    className='pointer margin-right-10'
                    color='primary'
                    onClick={(e) => this.showEditPhoneModal(row)}/>
                  <Delete
                    className='pointer'
                    color='primary'
                    onClick={(e) => this.removePhone(row)}/>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <TablePagination
          rowsPerPageOptions={[10, 25, 50]}
          component="div"
          count={this.state.count}
          rowsPerPage={this.state.rowsPerPage}
          page={this.state.page}
          backIconButtonProps={{
            'aria-label': 'Previous Page',
          }}
          nextIconButtonProps={{
            'aria-label': 'Next Page',
          }}
          onChangePage={this.handleChangePage}
          onChangeRowsPerPage={this.handleChangeRowsPerPage}
        />

        {this.state.showAddPhoneModal &&
        <PhoneDetailsModal
          handleClose={this.closeEditModal}/>
        }

        {/*{this.state.showRemovePhoneModal &&*/}
        {/*<RemoveModal*/}
        {/*  title={this.state.title}*/}
        {/*  message={this.state.message}*/}
        {/*  remove={this.removeSelectedPhone}*/}
        {/*  close={this.closeRemoveModal}/>*/}
        {/*}*/}

        {this.state.showEditPhoneModal &&
        <EditPhoneModal
          handleClose={this.closeEditPhoneModal}
          selectedPhone={this.state.selectedPhone}/>
        }

        {this.state.showBulkForwardingModal &&
        <EditPhonesForwardingModal
          handleClose={this.closeBulkForwardingModal}/>}
      </div>
    )
  }
}

export default Phones;
